import { Pipe, PipeTransform } from '@angular/core';
import { Result } from './model/result';
import { SearchResult } from './model/search-result';

@Pipe({
  name: 'searchResultToResult',
  standalone: true
})
export class SearchResultToResultPipe implements PipeTransform {

  transform(value: SearchResult): Result[] {
    return value ? [...value.existingPairs.map(i => Result.create(i)), ...value.availableSymbols ]: [];
  }

}
