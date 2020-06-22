import { Injectable } from '@angular/core';
import { SwUpdate } from '@angular/service-worker';

@Injectable({providedIn: 'root'})
export class PromptUpdateService {

  constructor(updates: SwUpdate) {
    console.log('PromptUpdateService');
    updates.available.subscribe(event => {
        updates.activateUpdate().then(() => document.location.reload());
    });
  }
}
