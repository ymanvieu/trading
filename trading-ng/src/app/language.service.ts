import { Injectable } from '@angular/core';
import { TranslateLoader } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { TRANSLATIONS } from '../assets/i18n';

@Injectable({ providedIn: 'root' })
export class LanguageService implements TranslateLoader {
  getTranslation(lang: string): Observable<any> {
    const translations = TRANSLATIONS[lang.toLowerCase()] || {};
    return of(translations);
  }
}
