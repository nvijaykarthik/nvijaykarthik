# Purging and Archiving the DB Analysis
## How do we partition the table in case we are planning to purge or archive (example every saturday)

The best approach is to use Range Partitioning by Date with weekly partitions. This will allow you to quickly drop old data without affecting performance.
## Recommended Partitioning Strategy 

1. Use Range Partitioning (By Date)

    Partition the table based on a DATE column (e.g., txn_date or created_at).
    Each partition will hold one week of data.
    When purging, simply drop the oldest partition, which is faster than deleting rows.

2. Example Partitioned Table
Create Table with Weekly Partitions
```
CREATE TABLE transactions (
    id NUMBER PRIMARY KEY,
    txn_date DATE NOT NULL,
    amount NUMBER(10,2),
    details VARCHAR2(255)
)
PARTITION BY RANGE (txn_date) INTERVAL (NUMTODSINTERVAL(7, 'DAY')) 
(
    PARTITION p_initial VALUES LESS THAN (TO_DATE('2025-02-01', 'YYYY-MM-DD'))
);
```
How This Works:

    - Data is automatically inserted into the correct partition based on txn_date.
    - New partitions will be created automatically every week.
    - The oldest partition can be dropped on Saturdays.

3. Weekly Purge (Dropping Old Partitions)

To remove data older than one week, drop the oldest partition:

```
ALTER TABLE transactions DROP PARTITION FOR (TO_DATE('2025-01-25', 'YYYY-MM-DD'));
```
Dropping a partition is instant and much faster than DELETE!

4. If You Need to Archive Before Purging

If you need to move data to an archive table before deleting:
```
ALTER TABLE transactions EXCHANGE PARTITION FOR (TO_DATE('2025-01-25', 'YYYY-MM-DD'))
WITH TABLE transactions_archive;
```
Then, drop the partition:
```
ALTER TABLE transactions DROP PARTITION FOR (TO_DATE('2025-01-25', 'YYYY-MM-DD'));
```
This ensures you don’t lose data before purging.
Why This is the Best Approach?

- No Full Table Scan – Dropping partitions is faster than deleting rows.
- Automatic Weekly Partitioning – No need to manually create new partitions.
- Improved Query Performance – Only relevant partitions are scanned.
- Easier Maintenance – Purging is as simple as dropping a partition.

## Other Partitioning Strategies

In addition to Range Partitioning, there are several other partitioning strategies available in Oracle databases:

1. **List Partitioning**

    List Partitioning allows you to partition a table based on discrete values. This is useful when you have a limited set of distinct values.

    Example:
    ```
    CREATE TABLE orders (
         order_id NUMBER PRIMARY KEY,
         order_date DATE,
         customer_id NUMBER,
         status VARCHAR2(20)
    )
    PARTITION BY LIST (status) (
         PARTITION p_new VALUES ('NEW'),
         PARTITION p_processing VALUES ('PROCESSING'),
         PARTITION p_shipped VALUES ('SHIPPED'),
         PARTITION p_completed VALUES ('COMPLETED')
    );
    ```

2. **Hash Partitioning**

    Hash Partitioning distributes data evenly across partitions using a hash function. This is useful for load balancing and ensuring even data distribution.

    Example:
    ```
    CREATE TABLE employees (
         emp_id NUMBER PRIMARY KEY,
         emp_name VARCHAR2(100),
         department_id NUMBER
    )
    PARTITION BY HASH (department_id) PARTITIONS 4;
    ```

3. **Composite Partitioning**

    Composite Partitioning combines two or more partitioning strategies. Common combinations include Range-Hash and Range-List partitioning.

    Example (Range-Hash):
    ```
    CREATE TABLE sales (
         sale_id NUMBER PRIMARY KEY,
         sale_date DATE,
         amount NUMBER(10,2),
         region VARCHAR2(50)
    )
    PARTITION BY RANGE (sale_date)
    SUBPARTITION BY HASH (region) SUBPARTITIONS 4
    (
         PARTITION p_2021 VALUES LESS THAN (TO_DATE('2022-01-01', 'YYYY-MM-DD')),
         PARTITION p_2022 VALUES LESS THAN (TO_DATE('2023-01-01', 'YYYY-MM-DD'))
    );
    ```

4. **Interval Partitioning**

    Interval Partitioning extends Range Partitioning by automatically creating new partitions as data arrives, based on a specified interval.

    Example:
    ```
    CREATE TABLE logs (
         log_id NUMBER PRIMARY KEY,
         log_date DATE,
         message VARCHAR2(255)
    )
    PARTITION BY RANGE (log_date) INTERVAL (NUMTODSINTERVAL(1, 'MONTH'))
    (
         PARTITION p_initial VALUES LESS THAN (TO_DATE('2022-01-01', 'YYYY-MM-DD'))
    );
    ```

5. **Reference Partitioning**

    Reference Partitioning allows you to partition a child table based on the partitioning of its parent table, maintaining referential integrity.

    Example:
    ```
    CREATE TABLE customers (
         customer_id NUMBER PRIMARY KEY,
         customer_name VARCHAR2(100)
    )
    PARTITION BY RANGE (customer_id) (
         PARTITION p1 VALUES LESS THAN (1000),
         PARTITION p2 VALUES LESS THAN (2000)
    );

    CREATE TABLE orders (
         order_id NUMBER PRIMARY KEY,
         order_date DATE,
         customer_id NUMBER,
         FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
    )
    PARTITION BY REFERENCE (customer_fk);
    ```

These partitioning strategies can be chosen based on the specific requirements of your data and query patterns. Each strategy offers different benefits in terms of performance, manageability, and scalability.

## Why is Range Partitioning Recommended?

Range partitioning is recommended for several reasons:

1. **Improved Query Performance**: It allows the database to quickly locate and access the relevant partitions, reducing the amount of data scanned and improving query performance.
2. **Efficient Data Management**: Simplifies data management tasks such as loading, deleting, and archiving data. For example, you can easily drop or archive old partitions without affecting the rest of the data.
3. **Enhanced Maintenance**: Maintenance operations like index rebuilding, statistics gathering, and backups can be performed on individual partitions rather than the entire table, making these operations faster and less resource-intensive.
4. **Better Load Balancing**: Distributes data more evenly across different storage devices, leading to better load balancing and optimized resource utilization.
5. **Improved Availability**: In case of partition-level failures, only the affected partition needs to be restored, which can improve the overall availability of the database.

## Moving Archived Data to a Secondary Database

To move archived data to a secondary database, you can use one of the following methods:

1. **Using Oracle Data Pump (EXPDP/IMPDP)**

    Export the archived data from the primary database:
    ```
    expdp system/password DIRECTORY=exp_dir DUMPFILE=transactions_archive.dmp TABLES=transactions_archive
    ```

    Import the archived data into the secondary database:
    ```
    impdp system/password DIRECTORY=imp_dir DUMPFILE=transactions_archive.dmp TABLES=transactions_archive REMAP_TABLESPACE=old_ts:new_ts
    ```

2. **Using Database Link**

    Create a database link to the secondary database:
    ```
    CREATE DATABASE LINK second_db_link
    CONNECT TO remote_user IDENTIFIED BY 'password'
    USING 'remote_tns';
    ```

    Move the archived data using the database link:
    ```
    INSERT INTO transactions_archive@second_db_link
    SELECT * FROM transactions_archive WHERE txn_date < SYSDATE - 30;

    COMMIT;
    ```

3. **Using Oracle GoldenGate**

    Set up Oracle GoldenGate for real-time replication of the archived data to the secondary database. This method requires additional configuration and licensing.

    ## Recommended Approach for Weekly Archiving

    For weekly archiving, the recommended approach is to use Oracle Data Pump (EXPDP/IMPDP) for batch transfers. This method is efficient for moving large volumes of data periodically and ensures that the archived data is safely stored in a secondary database or storage location.

    ### Steps for Weekly Archiving Using Data Pump

    1. **Export the Data**

        Use the `expdp` utility to export the data from the primary database. Schedule this export to run weekly.

        ```sql
        expdp system/password DIRECTORY=exp_dir DUMPFILE=transactions_archive_weekly.dmp TABLES=transactions_archive
        ```

    2. **Transfer the Dump File**

        Transfer the generated dump file (`transactions_archive_weekly.dmp`) to the secondary database or storage location.

    3. **Import the Data**

        Use the `impdp` utility to import the data into the secondary database.

        ```sql
        impdp system/password DIRECTORY=imp_dir DUMPFILE=transactions_archive_weekly.dmp TABLES=transactions_archive REMAP_TABLESPACE=old_ts:new_ts
        ```

    ### Automating the Process

    To automate the weekly archiving process, you can create a scheduled job using Oracle Scheduler.

    ```sql
    BEGIN
        DBMS_SCHEDULER.create_job (
            job_name        => 'WEEKLY_ARCHIVE_JOB',
            job_type        => 'PLSQL_BLOCK',
            job_action      => 'BEGIN
                                   expdp system/password DIRECTORY=exp_dir DUMPFILE=transactions_archive_weekly.dmp TABLES=transactions_archive;
                                   -- Add file transfer logic here
                                   impdp system/password DIRECTORY=imp_dir DUMPFILE=transactions_archive_weekly.dmp TABLES=transactions_archive REMAP_TABLESPACE=old_ts:new_ts;
                               END;',
            start_date      => SYSTIMESTAMP,
            repeat_interval => 'FREQ=WEEKLY; BYDAY=SAT; BYHOUR=2; BYMINUTE=0; BYSECOND=0',
            enabled         => TRUE
        );
    END;
    /
    ```

    This job will run every Saturday at 2 AM, performing the export, transfer, and import operations automatically.

    ### Benefits of Using Data Pump for Weekly Archiving

    - **Efficient for Large Data Volumes**: Handles large volumes of data efficiently.
    - **Automated Scheduling**: Can be easily automated using Oracle Scheduler.
    - **Consistent Data Transfer**: Ensures consistent and reliable data transfer between databases.
    - **Minimal Impact on Performance**: Runs during off-peak hours to minimize impact on database performance.

    Choose this approach to ensure a reliable and efficient weekly archiving process for your Oracle database.

    ## Why Oracle GoldenGate May Not Be Suitable

    While Oracle GoldenGate is a powerful tool for real-time data replication, it may not be the best choice for this specific situation due to the following reasons:

    1. **Cost**: Oracle GoldenGate requires additional licensing, which can be expensive. For organizations with budget constraints, this may not be a feasible option.

    2. **Complexity**: Setting up and maintaining Oracle GoldenGate can be complex and requires specialized knowledge. This adds to the administrative overhead and may require additional training for the database administrators.

    3. **Overhead**: Real-time replication can introduce additional overhead on the primary database, potentially impacting performance. For weekly archiving, this level of real-time replication may be unnecessary and inefficient.

    4. **Resource Intensive**: Oracle GoldenGate can be resource-intensive, requiring significant CPU, memory, and network bandwidth. This can be a concern for environments with limited resources.

    5. **Maintenance**: Regular maintenance and monitoring are required to ensure that the replication process is running smoothly. This can add to the operational burden on the IT team.

    Given these considerations, using Oracle Data Pump (EXPDP/IMPDP) for batch transfers is a more efficient and cost-effective approach for weekly archiving in this scenario.

    ## Why Database Link/Soft Link May Not Be Suitable

    While using a database link (also known as a soft link) can be a convenient method for moving data between databases, it may not be the best choice for weekly archiving due to the following reasons:

    1. **Performance Impact**: Transferring large volumes of data over a database link can significantly impact the performance of both the source and target databases. This is especially true if the link is used during peak hours.

    2. **Network Dependency**: Database links rely on network connectivity between the source and target databases. Any network issues can disrupt the data transfer process, leading to incomplete or failed transfers.

    3. **Security Concerns**: Using database links requires opening network connections between databases, which can introduce security risks. Proper security measures must be in place to protect sensitive data during transfer.

    4. **Limited Control**: Database links provide limited control over the data transfer process. For example, it can be challenging to manage and monitor the progress of large data transfers, and there is no built-in mechanism for handling errors or retries.

    5. **Resource Consumption**: Transferring data over a database link can consume significant resources (CPU, memory, and network bandwidth) on both the source and target databases. This can affect the performance of other operations running on these databases.

    6. **Complexity in Automation**: Automating the data transfer process using database links can be complex and may require custom scripts or procedures. This adds to the administrative overhead and increases the risk of errors.

    Given these considerations, using Oracle Data Pump (EXPDP/IMPDP) for batch transfers is a more efficient, secure, and manageable approach for weekly archiving in this scenario.

## Configuring and Automating Weekly Jobs for Multiple Tables with Foreign Key Relationships

To configure and automate the job for multiple tables with different retention periods, considering foreign key relationships, follow these steps:

1. **Create a Procedure for Purging and Archiving**

    Create a stored procedure that handles the purging and archiving logic for each table based on its retention period, using partition dropping and swapping to handle data efficiently.

    ```sql
        CREATE OR REPLACE PROCEDURE purge_and_archive AS
        BEGIN
            -- Archive and drop the oldest partition for Table1 (60-day retention)
            DECLARE
                partition_name VARCHAR2(50);
            BEGIN
                SELECT partition_name
                INTO partition_name
                FROM user_tab_partitions
                WHERE table_name = 'TABLE1'
                AND high_value < TO_DATE(SYSDATE - 60, 'YYYY-MM-DD')
                ORDER BY high_value
                FETCH FIRST 1 ROW ONLY;
                
                EXECUTE IMMEDIATE 'ALTER TABLE Table1 EXCHANGE PARTITION ' || partition_name || ' WITH TABLE Archive_table1';
                EXECUTE IMMEDIATE 'ALTER TABLE Table1 DROP PARTITION ' || partition_name;
            END;
            
            -- Archive and drop the oldest partition for Table2 (30-day retention)
            EXECUTE IMMEDIATE 'ALTER TABLE Table2 EXCHANGE PARTITION p_old_30_days WITH TABLE Archive_table2';
            EXECUTE IMMEDIATE 'ALTER TABLE Table2 DROP PARTITION p_old_30_days';
            
            -- Move archived data to secondary DB
            EXECUTE IMMEDIATE 'INSERT INTO Archive_table2@second_db_link SELECT * FROM Archive_table2 WHERE txn_date < SYSDATE - 30';
            EXECUTE IMMEDIATE 'DELETE FROM Archive_table2 WHERE txn_date < SYSDATE - 30';
            
            -- Repeat similar steps for other tables, ensuring foreign key constraints are handled
            -- ...
        END;
     ```

2. **Create a Scheduled Job Using Oracle Scheduler**

    Use Oracle Scheduler to create a job that runs the procedure every Saturday at 10 PM.

    ```sql
    BEGIN
        DBMS_SCHEDULER.create_job (
            job_name        => 'WEEKLY_PURGE_ARCHIVE_JOB',
            job_type        => 'PLSQL_BLOCK',
            job_action      => 'BEGIN purge_and_archive; END;',
            start_date      => SYSTIMESTAMP,
            repeat_interval => 'FREQ=WEEKLY; BYDAY=SAT; BYHOUR=22; BYMINUTE=0; BYSECOND=0',
            enabled         => TRUE
        );
    END;
    /
    ```

3. **Automate Data Transfer to Secondary Database**

    Ensure that the database link to the secondary database is created and accessible.

    ```sql
    CREATE DATABASE LINK second_db_link
    CONNECT TO remote_user IDENTIFIED BY 'password'
    USING 'remote_tns';
    ```

4. **Monitor and Maintain the Job**

    Regularly monitor the job's execution and maintain the procedure to handle any changes in the table structure or retention policies.

    ```sql
    SELECT job_name, status, run_duration
    FROM dba_scheduler_job_run_details
    WHERE job_name = 'WEEKLY_PURGE_ARCHIVE_JOB';
    ```

By following these steps, you can configure and automate the purging and archiving process for multiple tables with different retention periods, ensuring efficient data management and compliance with retention policies while respecting foreign key relationships.


## Impact of Range Partitioning on DML and Queries

### Select Queries

**Benefits:**
- **Improved Performance**: Range partitioning can significantly improve the performance of select queries by allowing the database to scan only the relevant partitions instead of the entire table.
- **Partition Pruning**: The database can eliminate partitions that do not match the query criteria, reducing the amount of data scanned.

**Considerations:**
- **Query Optimization**: Ensure that queries are written to take advantage of partition pruning by including the partition key in the WHERE clause.

### Insert Queries

**Benefits:**
- **Efficient Data Insertion**: Data is automatically directed to the appropriate partition based on the partition key, making inserts efficient.

**Considerations:**
- **Partition Maintenance**: Ensure that new partitions are created as needed to accommodate incoming data. This can be automated with interval partitioning.

### Update Queries

**Benefits:**
- **Localized Updates**: Updates that affect a single partition can be more efficient than those affecting the entire table.

**Considerations:**
- **Partition Key Updates**: Updating the partition key can be expensive as it may require moving the row to a different partition. Avoid updating the partition key if possible.

### Delete Queries

**Benefits:**
- **Efficient Deletion**: Dropping a partition is much faster than deleting rows individually, making it efficient for bulk deletions.

**Considerations:**
- **Partition Dropping**: Ensure that the partition to be dropped does not contain any data that needs to be retained.

### Aggregate Queries

**Benefits:**
- **Parallel Processing**: Aggregate queries can benefit from parallel processing across partitions, improving performance.

**Considerations:**
- **Indexing**: Ensure that appropriate indexes are in place to support aggregate queries and take advantage of partitioning.

## Conclusion

In summary, implementing a robust partitioning strategy, particularly range partitioning, can significantly enhance the performance and manageability of your Oracle database. By automating the purging and archiving processes, you can ensure efficient data management and compliance with retention policies. Additionally, understanding the impact of partitioning on various DML operations and queries will help you optimize your database for better performance and scalability.

### Key Takeaways

- **Range Partitioning**: Recommended for its efficiency in managing large datasets and improving query performance.
- **Automated Archiving**: Use Oracle Data Pump for efficient weekly archiving and Oracle Scheduler for automation.
- **Impact on DML and Queries**: Range partitioning improves performance but requires careful design and monitoring.

By following the guidelines and strategies outlined in this document, you can achieve a well-partitioned, high-performing Oracle database that meets your data management needs.

### Next Steps

1. **Implement Partitioning**: Start by partitioning your tables based on the recommended strategies.
2. **Automate Processes**: Set up automated jobs for purging and archiving.
3. **Monitor Performance**: Regularly monitor the impact of partitioning on your database performance and make adjustments as needed.

For further assistance or detailed implementation support, consider consulting with a database expert or Oracle support.

### Overall Impact
Range partitioning generally improves the performance and manageability of DML operations and queries. However, it is essential to design the partitioning strategy carefully and monitor the system to ensure that it meets performance and maintenance requirements.
