import { Pair } from './pair';

export class Result {

  static create(pair: Pair): Result {
    return new Result(pair.symbol, pair.name, pair.exchange, null, pair.providerCode, pair.id, pair.lastUpdate);
  }

  constructor(
      public code: string,
      public name: string,
      public exchange: string,
      public type: string,
      public providerCode: string,
      public id?: number,
      public lastUpdate?: Date) {}
}
