import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SearchResult } from './model/search-result';

@Injectable({providedIn: 'root'})
export class AdminService {

  private readonly url = '/api/admin';

  constructor(private http: HttpClient) {}

  getSymbols(search?: string): Observable<SearchResult> {
    return this.http.get<SearchResult>(`${this.url}?code=${search}`);
  }

  addSymbol(code: string, provider: string): Observable<any> {
    return this.http.post(`${this.url}/${provider}/${code}`, {});
  }

  removeSymbol(symbol: string, provider: string): Observable<any> {
    return this.http.delete(`${this.url}/${provider}/${symbol}`);
  }
}
