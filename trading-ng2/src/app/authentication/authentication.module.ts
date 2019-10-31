import { NgModule } from '@angular/core';
import { AuthModule, AUTH_SERVICE, PUBLIC_FALLBACK_PAGE_URI, PROTECTED_FALLBACK_PAGE_URI} from 'ngx-auth';
import { AuthenticationService } from './authentication.service';
import { HttpClient } from '@angular/common/http';

export function factory(authenticationService: AuthenticationService) {
  return authenticationService;
}

@NgModule({
    imports: [ AuthModule],
    providers: [
      HttpClient,
      { provide: PROTECTED_FALLBACK_PAGE_URI, useValue: '/' },
      { provide: PUBLIC_FALLBACK_PAGE_URI, useValue: '/login' },
      {
        provide: AUTH_SERVICE,
        deps: [ AuthenticationService ],
        useFactory: factory
      }
    ]
})
export class AuthenticationModule {

}
