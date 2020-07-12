import { HttpClient } from '@angular/common/http';
import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subscription } from 'rxjs';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'client';
  message = '';
  messages: any[];
  sub: Subscription;

  constructor(private zone: NgZone, private http: HttpClient) {
  }

  getMessages(): Observable<any> {

    return Observable.create(
      observer => {

        let source = new EventSource("http://localhost:8080/messages");
        source.onmessage = event => {
          this.zone.run(() => {
            observer.next(event.data)
          })
        }

        source.onerror = event => {
          this.zone.run(() => {
            observer.error(event)
          })
        }
      }
    )
  }

  ngOnInit(): void {
    this.messages = [];
    this.sub = this.getMessages().subscribe({
      next: data => {
        console.log(data);
        this.addMessage(data);
      },
      error: err => console.error(err)
    });
  }

  addMessage(msg: any) {
    this.messages = [...this.messages, msg];
    //console.log("messages::" + this.messages);
  }

  ngOnDestroy(): void {
    this.sub && this.sub.unsubscribe();
  }

  sendMessage() {
    console.log("sending message:" + this.message);
    this.http
      .post(
        "http://localhost:8080/messages",
        this.message
      )
      .subscribe({
        next: (data) => console.log(data),
        error: (error) => console.log(error),
        complete: () => {
          console.log('complete');
          this.message = '';
        }
      });

  }
}
