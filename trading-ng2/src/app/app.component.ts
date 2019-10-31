import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { RxStompService } from '@stomp/ng2-stompjs';
import { distinctUntilChanged, tap } from 'rxjs/operators';
import { AuthenticationService } from './authentication';
import { PortofolioService } from './portofolio';
import { TokenStorage } from './authentication/token-storage.service';
import { PromptUpdateService } from './prompt-update.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(
    private authService: AuthenticationService,
    private portofolioService: PortofolioService,
    translateService: TranslateService,
    private rxStompService: RxStompService,
    private tokenStorage: TokenStorage,
    promptUpdateService: PromptUpdateService
    ) {
      translateService.setDefaultLang('en');
    }

  ngOnInit(): void {
    this.authService.refreshUser();

    this.authService.getUser()
      .pipe(
        tap(login => console.log('login: ', login)),
        distinctUntilChanged())
      .subscribe(login => {
        if (!!login) {
          this.portofolioService.refreshPortofolioAndAvailableSymbols();
        } else {
          this.portofolioService.clear();
        }
      });

      this.tokenStorage.getAccessToken()
        .subscribe(token => {
          if (!!token) {
            const headers = {};
            headers['Authorization'] = token;

            this.rxStompService.configure({ connectHeaders: headers });
          } else {
            this.rxStompService.configure({ connectHeaders: null });
          }

          this.rxStompService.stompClient.forceDisconnect();
        });
  }
}
