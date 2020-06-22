import { Symbol } from '../../symbol/model/symbol';

export class Pair {
    symbol: string;
    name: string;
    source: Symbol;
    target: Symbol;
    exchange: string;
    providerCode: string;
    lastUpdate: Date;
}
