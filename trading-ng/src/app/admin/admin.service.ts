import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Pair } from './model/pair';
import { SearchResult } from './model/search-result';
import { PairInfo } from './model/pair-info';

@Injectable({providedIn: 'root'})
export class AdminService {

  private static readonly ADMIN_URL = '/api/admin';

  constructor(private http: HttpClient) {}

  getSymbols(search?: string): Observable<SearchResult> {
    return this.http.get<SearchResult>(AdminService.ADMIN_URL, {params: {code: search}});
  }

  addPair(code: string, provider: string): Observable<PairInfo> {
    return this.http.post<PairInfo>(`${AdminService.ADMIN_URL}/${provider}/${code}`, {});
  }

  removePair(pairId: number, withSymbol: boolean): Observable<any> {
    return this.http.delete(`${AdminService.ADMIN_URL}/${pairId}?withSymbol=${withSymbol}`);
  }

  updatePair(pair: Pair): Observable<PairInfo> {
    return this.http.put<PairInfo>(`${AdminService.ADMIN_URL}`, pair);
  }
}
