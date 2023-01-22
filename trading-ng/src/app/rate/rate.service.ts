import { Rate } from './model/rate';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({providedIn: 'root'})
export class RateService {

  private readonly url = '/api/rate';

  constructor(private http: HttpClient) { }

  getLatest(): Observable<Rate[]> {
    return this.http.get<Rate[]>(`${this.url}/latest`);
  }

  getLatestFrom(fromcur: string, tocur: string): Observable<Rate> {
    return this.http.get<Rate>(`${this.url}/latest`, {params: {fromcur, tocur}});
  }

  getHistoryFrom(fromcur: string, tocur: string, startDate?: Date, endDate?: Date): Observable<number[][]> {
    let params = {};

    if (startDate && endDate) {
      params = {fromcur, tocur, startDate: startDate.toISOString(), endDate: endDate.toISOString()};
    } else {
      params = {fromcur, tocur};
    }

    return this.http.get<number[][]>(`${this.url}/history`, {params: params});
  }
}
