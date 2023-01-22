export class Pair {
  constructor(
      public id: number,
      public symbol: string,
      public name: string,
      public sourceCode: string,
      public targetCode: string,
      public exchange: string,
      public providerCode: string,
      public lastUpdate: Date) {
  }

}
