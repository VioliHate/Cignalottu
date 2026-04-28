import { Component, ComponentRef, Inject, inject, Optional } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComponentPortal, PortalModule } from '@angular/cdk/portal';
import { DialogConfig } from '../../utils/dialog/dialog-config';
import { DialogRef } from '../../utils/dialog/dialog-ref';
import { DIALOG_DATA } from '../../utils/dialog/dialog-tokens';
import { LucideAngularModule, LucideIconData, Mail, User, XIcon } from 'lucide-angular';

@Component({
  selector: 'app-dialog',
  imports: [CommonModule, PortalModule, LucideAngularModule],
  templateUrl: './app-dialog.html',
  styleUrl: './app-dialog.css',
})
export class AppDialog {
  protected readonly xIcon = XIcon;
  portal: ComponentPortal<any>;
  dialogRef = inject(DialogRef);

  constructor(@Optional() @Inject(DIALOG_DATA) public config: DialogConfig) {
    if (!config?.component) {
      throw new Error('Devi passare un componente da mostrare!');
    }

    this.portal = new ComponentPortal(config.component);
  }

  close(result?: any) {
    console.log('4. DialogRef sta chiudendo. Result:', result);
    console.log("5. L'istanza di DialogRef è:", this);
    this.dialogRef.close(result);
  }

  onAttached(ref: any) {
    console.log('Componente attaccato:', ref);

    if (ref instanceof ComponentRef) {
      if (ref.instance.formSubmit) {
        ref.instance.formSubmit.subscribe((data: any) => {
          console.log('2. Dati arrivati a AppDialog:', data);
          console.log("3. L'istanza di DialogRef che sto per chiudere è:", this.dialogRef);
          this.dialogRef.close(data);
        });
      } else {
        console.error('ERRORE: formSubmit non trovato sul componente!');
      }
    }
  }

  protected readonly mailIcon = Mail;
}
