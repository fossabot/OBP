export class Util {
  static camelToReg(s: string) {
    return s.replace(/([A-Z])/g, ' $1')
      .replace(/^./, function (str) {
        return str.toUpperCase();
      });
  }

  static capitalizeFirstLetter(s) {
    return s.charAt(0).toUpperCase() + s.slice(1);
  }
}
