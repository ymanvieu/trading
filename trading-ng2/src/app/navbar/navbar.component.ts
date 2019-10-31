import {AuthenticationService} from '../authentication';
import {Asset} from '../portofolio/model/asset';
import {PortofolioService} from '../portofolio/portofolio.service';
import {Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  baseCurrency: Asset;
  login: string;

  constructor(
    private authService: AuthenticationService,
    private portofolioService: PortofolioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.portofolioService.getPortofolio().subscribe(p => this.baseCurrency = !!p ? p.baseCurrency : null);
    this.authService.getUser().subscribe(login => this.login = login);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

}
