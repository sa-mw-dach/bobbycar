import { ConfigService } from './config.service';
import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';


@Injectable({
  providedIn: 'root'
})
export class WSService {

  private socket$: WebSocketSubject<any>;
  private WS_ENDPOINT: string;

  constructor(private configService: ConfigService) {
    console.debug('new WSService()');
    this.WS_ENDPOINT = configService.WS_ENDPOINT;
  }

  public connect(): void {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = this.getNewWebSocket();
    }
  }

  private getNewWebSocket() {
    return webSocket(this.WS_ENDPOINT);
  }

  getMessages() {
    return this.socket$.asObservable();
  }
  sendMessage(msg: any) {
    this.socket$.next(msg);
  }
  close() {
    this.socket$.complete(); }
}