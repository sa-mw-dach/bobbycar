import { ConfigService } from './config.service';
import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class CarMetricsAggregatedService {

  private socket$: WebSocketSubject<any>;
  private CAR_METRICS_AGGREGATED_ENDPOINT: string;

  constructor(private configService: ConfigService) {
    console.debug('new CarMetricsAggregatedService()');
    this.CAR_METRICS_AGGREGATED_ENDPOINT = configService.CAR_METRICS_AGGREGATED_ENDPOINT;
  }

  public connect(): void {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = this.getNewWebSocket();
    }
  }

  private getNewWebSocket(): WebSocketSubject<any> {
    return webSocket(this.CAR_METRICS_AGGREGATED_ENDPOINT);
  }

  getMessages(): Observable<any> {
    return this.socket$.asObservable();
  }
  sendMessage(msg: any) {
    this.socket$.next(msg);
  }
  close(): void {
    this.socket$?.complete(); }
}
