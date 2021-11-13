import { InjectableRxStompConfig } from '@stomp/ng2-stompjs';

import * as SockJS from 'sockjs-client';


export function socketProvider() {
    return new SockJS('/stomp');
  }

export const appStompConfig: InjectableRxStompConfig = {
  // Which server?
  // brokerURL: 'ws://127.0.0.1:15674/ws',
  webSocketFactory: socketProvider,

  // Headers
  // Typical keys: login, passcode, host
//   connectHeaders: {
//     login: 'guest',
//     passcode: 'guest'
//   },

  // How often to heartbeat?
  // Interval in milliseconds, set to 0 to disable
  heartbeatIncoming: 0, // Typical value 0 - disabled
  heartbeatOutgoing: 20000, // Typical value 20000 - every 20 seconds

  // Wait in milliseconds before attempting auto reconnect
  // Set to 0 to disable
  // Typical value 500 (500 milli seconds)
  reconnectDelay: 500,

  // https://stomp-js.github.io/guide/stompjs/rx-stomp/upgrading-to-stompjs-6-rx-stomp-1.html
  discardWebsocketOnCommFailure: true,
  connectionTimeout: 0,

  // Will log diagnostics on console
  // It can be quite verbose, not recommended in production
  // Skip this key to stop logging to console
  debug: (msg: string): void => {
    // console.log(new Date(), msg);
  }
};
