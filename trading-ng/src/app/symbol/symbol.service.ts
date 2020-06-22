import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({providedIn: 'root'})
export class SymbolService {

    private readonly url = '/api/symbol';

    constructor(
        private http: HttpClient
    ) { }

    addFavoriteSymbol(fromSymbolCode: string, toSymbolCode: string): Observable<void> {
        return this.http.post<void>(`${this.url}/favorite`, {fromSymbolCode, toSymbolCode});
    }

    deleteFavoriteSymbol(fromSymbolCode: string, toSymbolCode: string): Observable<void> {
        return this.http.delete<void>(`${this.url}/favorite/${fromSymbolCode}/${toSymbolCode}`);
    }
}
