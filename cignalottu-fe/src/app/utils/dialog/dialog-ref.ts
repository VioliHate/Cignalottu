import { OverlayRef } from '@angular/cdk/overlay';
import { Subject, Observable } from 'rxjs';

export class DialogRef {
  private _afterClosed = new Subject<any>();

  constructor(private overlayRef: OverlayRef) {}

  close(result?: any) {
    this.overlayRef.dispose();
    this._afterClosed.next(result);
    this._afterClosed.complete();
  }

  afterClosed(): Observable<any> {
    return this._afterClosed.asObservable();
  }
}
