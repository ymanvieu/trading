import { Injectable } from '@angular/core';
import { SwUpdate } from '@angular/service-worker';

@Injectable({providedIn: 'root'})
export class PromptUpdateService {

  constructor(updates: SwUpdate) {
    console.log('PromptUpdateService');
    updates.versionUpdates.subscribe(event => {

      switch (event.type) {
        case 'VERSION_DETECTED':
          console.log(`Downloading new app version: ${event.version.hash}`);
          break;
        case 'VERSION_READY':
          updates.activateUpdate().then(() => document.location.reload());
          break;
        case 'NO_NEW_VERSION_DETECTED':
          console.log('No new version detected');
          break;
      }
    });
  }
}
