import { Rate } from './model/rate';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({providedIn: 'root'})
export class RateService {

  private readonly url = '/api/rate';

  constructor(private http: HttpClient) { }

  getLatest(): Observable<Rate[]> {
    return this.http.get<Rate[]>(this.url);
  }

  getRaw(fromcur: string, tocur: string, startDate?: Date, endDate?: Date): Observable<any[][]> {
    let url: string;

    if (startDate && endDate) {
      url = `${this.url}/raw?fromcur=${fromcur}&tocur=${tocur}&startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}`;
    } else {
      url = `${this.url}/raw?fromcur=${fromcur}&tocur=${tocur}`;
    }

    return this.http.get<any[][]>(url);
  }

  getLatestFrom(fromcur: string, tocur: string): Observable<Rate> {
    return this.http.get<Rate>(`${this.url}/latest?fromcur=${fromcur}&tocur=${tocur}`);
  }
}
