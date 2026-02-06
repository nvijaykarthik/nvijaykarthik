export class ColorUtils {
  /**
   * Generate color shades from a base color using HSL
   * Returns an object with shades from 50-900
   */
  static generateColorShades(baseColor: string): Record<string, string> {
    // Convert hex to HSL
    const hsl = this.hexToHsl(baseColor);
    
    // Generate shades
    return {
      50: this.hslToHex(hsl.h, hsl.s, 0.97),  // Very light
      100: this.hslToHex(hsl.h, hsl.s, 0.94),
      200: this.hslToHex(hsl.h, hsl.s, 0.86),
      300: this.hslToHex(hsl.h, hsl.s, 0.77),
      400: this.hslToHex(hsl.h, hsl.s, 0.66),
      500: baseColor,  // Base color
      600: this.hslToHex(hsl.h, hsl.s, hsl.l * 0.85),
      700: this.hslToHex(hsl.h, hsl.s, hsl.l * 0.75),
      800: this.hslToHex(hsl.h, hsl.s, hsl.l * 0.65),
      900: this.hslToHex(hsl.h, hsl.s, hsl.l * 0.55)
    };
  }

  /**
   * Generate color shades with more control
   */
  static generateAdvancedShades(
    baseColor: string, 
    options: {
      lightSaturation?: number;  // 0-1
      darkSaturation?: number;   // 0-1
      lightStep?: number;        // Lightness increment for lighter shades
      darkStep?: number;         // Lightness decrement for darker shades
    } = {}
  ): Record<string, string> {
    const defaultOptions = {
      lightSaturation: 0.95,
      darkSaturation: 1.0,
      lightStep: 0.08,
      darkStep: 0.08
    };
    
    const config = { ...defaultOptions, ...options };
    const hsl = this.hexToHsl(baseColor);
    
    return {
      50: this.hslToHex(hsl.h, hsl.s * config.lightSaturation, 0.97),
      100: this.hslToHex(hsl.h, hsl.s * config.lightSaturation, 0.94),
      200: this.hslToHex(hsl.h, hsl.s * config.lightSaturation, 0.86),
      300: this.hslToHex(hsl.h, hsl.s * config.lightSaturation, 0.77),
      400: this.hslToHex(hsl.h, hsl.s * config.lightSaturation, 0.66),
      500: baseColor,
      600: this.hslToHex(hsl.h, hsl.s * config.darkSaturation, hsl.l * 0.85),
      700: this.hslToHex(hsl.h, hsl.s * config.darkSaturation, hsl.l * 0.75),
      800: this.hslToHex(hsl.h, hsl.s * config.darkSaturation, hsl.l * 0.65),
      900: this.hslToHex(hsl.h, hsl.s * config.darkSaturation, hsl.l * 0.55)
    };
  }

  // Helper: Convert Hex to HSL
  private static hexToHsl(hex: string): { h: number; s: number; l: number } {
    // Remove # if present
    hex = hex.replace('#', '');
    
    // Parse RGB
    const r = parseInt(hex.substring(0, 2), 16) / 255;
    const g = parseInt(hex.substring(2, 4), 16) / 255;
    const b = parseInt(hex.substring(4, 6), 16) / 255;
    
    const max = Math.max(r, g, b);
    const min = Math.min(r, g, b);
    
    let h = 0;
    let s = 0;
    const l = (max + min) / 2;
    
    if (max !== min) {
      const d = max - min;
      s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
      
      switch (max) {
        case r: h = (g - b) / d + (g < b ? 6 : 0); break;
        case g: h = (b - r) / d + 2; break;
        case b: h = (r - g) / d + 4; break;
      }
      h /= 6;
    }
    
    return { h: h * 360, s, l };
  }

  // Helper: Convert HSL to Hex
  private static hslToHex(h: number, s: number, l: number): string {
    h /= 360;
    let r: number, g: number, b: number;
    
    if (s === 0) {
      r = g = b = l; // achromatic
    } else {
      const hue2rgb = (p: number, q: number, t: number) => {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1/6) return p + (q - p) * 6 * t;
        if (t < 1/2) return q;
        if (t < 2/3) return p + (q - p) * (2/3 - t) * 6;
        return p;
      };
      
      const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
      const p = 2 * l - q;
      
      r = hue2rgb(p, q, h + 1/3);
      g = hue2rgb(p, q, h);
      b = hue2rgb(p, q, h - 1/3);
    }
    
    const toHex = (x: number) => {
      const hex = Math.round(x * 255).toString(16);
      return hex.length === 1 ? '0' + hex : hex;
    };
    
    return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
  }
}