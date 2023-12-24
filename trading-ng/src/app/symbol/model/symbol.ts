export class Symbol {
  code: string;
  name: string;
  countryFlag: string;

  constructor(obj?: Partial<Symbol>) {
    Object.assign(this, obj);
  }
}
