import { OverlayRef } from '@angular/cdk/overlay';
import { Subject } from 'rxjs';

export class DialogRef<T = unknown> {

  private closed$ = new Subject<T | undefined>();

  constructor(private overlayRef: OverlayRef) {}

  close(result?: T) {
    this.overlayRef.dispose();
    this.closed$.next(result);
    this.closed$.complete();
  }

  afterClosed() {
    return this.closed$.asObservable();
  }

}
