import { ConfigService } from './config.service';
import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SpeedAlertService {

  private socket$: WebSocketSubject<any>;
  private SPEED_ALERT_ENDPOINT: string;

  constructor(private configService: ConfigService) {
    console.debug('new SpeedAlertService()');
    this.SPEED_ALERT_ENDPOINT = configService.SPEED_ALERT_ENDPOINT;
  }

  public connect(): void {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = this.getNewWebSocket();
    }
  }

  private getNewWebSocket(): WebSocketSubject<any> {
    return webSocket(this.SPEED_ALERT_ENDPOINT);
  }

  getMessages(): Observable<any> {
    return this.socket$.asObservable();
  }
  sendMessage(msg: any) {
    this.socket$.next(msg);
  }
  close(): void {
    this.socket$.complete(); }
}