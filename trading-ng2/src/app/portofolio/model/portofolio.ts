import { Asset } from './asset';
export class Portofolio {

  baseCurrency: Asset;
  assets: Asset[];
  currentValue: number;
  percentChange: number;
  valueChange: number;
}
