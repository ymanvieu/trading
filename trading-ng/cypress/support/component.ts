// ***********************************************************
// This example support/component.ts is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Provider, Type } from '@angular/core';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';

// Alternatively you can use CommonJS syntax:
// require('./commands')

import { mount, MountConfig } from 'cypress/angular';
import { LanguageService } from '../../src/app/language.service';

// Augment the Cypress namespace to include type definitions for
// your custom command.
// Alternatively, can be defined in cypress/support/component.d.ts
// with a <reference path="./component" /> at the top of your spec.
declare global {
  namespace Cypress {
    interface Chainable {
      mount: typeof mount
    }
  }
}

const imports = [
  HttpClientModule,
  TranslateModule.forRoot({
    defaultLanguage: 'en',
    loader: { provide: TranslateLoader, useClass: LanguageService },
  }),
  NoopAnimationsModule
];

const providers: Provider[] = [HttpClient];

function customMount<T>(component: string | Type<T>, config?: MountConfig<T>) {
  if (!config) {
    config = { imports, providers };
  } else {
    config.imports = [...(config?.imports || []), ...imports];
    config.providers = [...(config?.providers || []), ...providers];
  }

  return mount<T>(component, config);
}

Cypress.Commands.add('mount', customMount);
