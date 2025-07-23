import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Contacto} from '../../../model/contacto';
import {CommonModule} from '@angular/common';
import {EmailService} from '../../../servicios/email.service';

@Component({
  selector: 'app-contacto-init',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule],
  templateUrl: './contacto-init.component.html',
  styleUrl: './contacto-init.component.css'
})
export class ContactoInitComponent  {
  protected form: FormGroup;
  protected enProceso:boolean = false;
  protected mensajeExito = false;
  protected mensajeError:boolean = false;
  private emailService:EmailService=inject(EmailService);

  constructor(private formBuilder: FormBuilder) {
    this.form = this.formBuilder.group({
      nombre: ['', [Validators.required]],
      apellido: ['', [Validators.required]],
      email : ['', Validators.compose([
        Validators.required,
        Validators.email
      ])],
      telefono: ['', Validators.compose([
        Validators.required,
        Validators.maxLength(20)
      ])],
      motivo: ['', [Validators.required]],
      descripcion: ['', [Validators.required]]
    })
  }



  enviarFormulario(): void {
    if (this.form.invalid) {
      this.mensajeError = true;
      return;
    }

    this.enProceso = true;
    this.mensajeExito = false;
    this.mensajeError = false;

    const contacto = this.form.value;

    this.emailService.enviarContacto(contacto).subscribe({
      next: (res) => {
        this.enProceso = false;
        this.mensajeExito = true;
        this.form.reset();

        setTimeout(() => {
          this.mensajeExito = false;
        }, 5000);
      },
      error: (err) => {
        this.enProceso = false;
        this.mensajeError = true;
        console.error('Error al enviar contacto', err);
      }
    });
  }

}
