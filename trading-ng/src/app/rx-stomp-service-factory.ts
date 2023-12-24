import { appStompConfig } from './app-stomp.config';
import { RxStompService } from './rx-stomp.service';

export function rxStompServiceFactory() {
  const rxStomp = new RxStompService();
  rxStomp.configure(appStompConfig);
  rxStomp.activate();
  return rxStomp;
}
