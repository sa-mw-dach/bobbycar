import { ConfigService } from './config.service';
import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class ZoneChangeService {

  private socket$: WebSocketSubject<any>;
  private CAR_ZONECHANGE_ENDPOINT: string;

  constructor(private configService: ConfigService) {
    console.debug('new ZoneChangeService()');
    this.CAR_ZONECHANGE_ENDPOINT = configService.CAR_ZONECHANGE_ENDPOINT;
  }

  public connect(): void {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = this.getNewWebSocket();
    }
  }

  private getNewWebSocket(): WebSocketSubject<any> {
    return webSocket(this.CAR_ZONECHANGE_ENDPOINT+'?user_key='+this.configService.BOBBYCAR_API_KEY);
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