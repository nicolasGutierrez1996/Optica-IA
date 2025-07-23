import {Component, EventEmitter, inject, Input, OnInit, Output} from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ProvinciaService } from '../../servicios/provincia.service';
import { OpticaService } from '../../servicios/optica.service';
import { Provincia } from '../../model/provincia';
import { Localidad } from '../../model/localidad';
import { Direccion } from '../../model/direccion';
import { Optica } from '../../model/optica';
import {UsuarioService} from '../../servicios/usuario.service';
import {Usuario} from '../../model/usuario';
import {ConfirmarPasswordComponent} from '../confirmar-password/confirmar-password.component';

@Component({
  selector: 'app-datos-optica',
  standalone: true,
  imports: [ReactiveFormsModule, ConfirmarPasswordComponent],
  templateUrl: './datos-optica.component.html',
  styleUrl: './datos-optica.component.css'
})
export class DatosOpticaComponent implements OnInit {
  protected form: FormGroup;
  protected previewUrl: any;
  protected selectedFile: File | null = null;
  protected mensajeError: string = '';
  protected enviando: boolean = false;
  protected subiendoLogo: boolean = false;
  protected logoUrlSubido: string = '';
  protected listaProvincia: Provincia[] = [];
  protected listaLocalidad: Localidad[] = [];
  protected localidadesFiltradas: Localidad[] = [];
  protected provinciaSeleccionadaId: number = 0;
  @Output() registroCompletado = new EventEmitter<Optica>();
  @Output() esEdicion = new EventEmitter<boolean>();
  private usuarioService: UsuarioService=inject(UsuarioService);
  @Input() modoEdicion!: boolean;
  private optica_id:number=0;
  protected editando: boolean = false;
  protected logoSeleccionado: boolean=false;
  protected mensajeExito: string='';
  protected tieneSuscripcion: boolean=false;
  protected tienePlanBasico:boolean=false;
  protected mostrarConfirmacion: boolean=false;


  constructor(
    private fb: FormBuilder,
    private provinciaService: ProvinciaService,
    private opticaService: OpticaService
  ) {
    this.form = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
      telefono: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(50),
        Validators.pattern(/^\d+$/)
      ]],
      descripcion: ['', [Validators.maxLength(500)]],
      direccion: this.fb.group({
        provincia: [null, [Validators.required]],
        localidad: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
        calle: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
        numero: [null, [Validators.min(1), Validators.pattern('^[0-9]+$')]],
        piso: ['', [Validators.maxLength(10)]],
        depto: ['', [Validators.maxLength(10)]],
      })
    });

  }

  ngOnInit(): void {
    this.usuarioService.obtenerMiPerfil().subscribe({
      next: (resp) => {
        const usuario = resp.data as Usuario | undefined;
        if (!usuario) {
          alert('No se pudo obtener el perfil del usuario');
          return;
        }

        // Guardamos el ID de la óptica si está en modo edición
        if (usuario.optica?.id) {
          this.optica_id = usuario.optica.id;
        }

        if (usuario.optica && !this.modoEdicion) {
          this.registroCompletado.emit(usuario.optica);
        } else if (this.modoEdicion && usuario.optica) {
          // modo edición: cargar provincias y datos de la óptica
          this.provinciaService.listarProvincias().subscribe(response => {
            this.listaProvincia = response;

            this.previewUrl = `/api/archivos${usuario.optica!.logoUrl!}`;
            this.logoUrlSubido = usuario.optica!.logoUrl!;
            this.logoSeleccionado = true;

            this.cargarDatosOptica(usuario.optica!);
            this.verificarSuscripcion(usuario.id!);

          });
        } else {
          // solo cargar provincias
          this.provinciaService.listarProvincias().subscribe(response => {
            this.listaProvincia = response;
          });
        }
      },
      error: () => {
        alert('Error al cargar el perfil del usuario.');
      }
    });
  }

  onProvinciaSeleccionada(event: Event): void {
    const selectedId = +(event.target as HTMLSelectElement).value;
    this.provinciaSeleccionadaId = selectedId;

    this.provinciaService.obtenerLocalidades(selectedId).subscribe(localidades => {
      const unicasYOrdenadas = localidades
        .filter((loc, index, self) =>
          self.findIndex(l => l.nombre.toLowerCase() === loc.nombre.toLowerCase()) === index
        )
        .sort((a, b) => a.nombre.localeCompare(b.nombre));

      this.listaLocalidad = unicasYOrdenadas;
      this.localidadesFiltradas = unicasYOrdenadas;
      this.form.get('direccion.localidad')?.setValue('');
    });
  }




  onFileSelected(event: Event): void {
    this.subiendoLogo = true;
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const archivo = input.files[0];

      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result as string;
      };
      reader.readAsDataURL(archivo);
      this.selectedFile = archivo;

      this.opticaService.subirLogo(archivo).subscribe({
        next: (res) => {
          if (res.success) {
            this.logoUrlSubido = res.data;
          } else {
            this.mensajeError = res.message ?? 'Error al subir el logo';
          }
          this.subiendoLogo = false;
        },
        error: (err) => {
          this.mensajeError = err.error?.mensaje ?? 'Error inesperado al subir logo';
          this.subiendoLogo = false;
        }
      });
    }
  }

  enviar(): void {
    this.enviando = true;
    this.mensajeError = '';



    this.opticaService.guardar(this.construirOpticaDesdeFormulario()).subscribe({
      next: (res) => {
        if (res.success) {
          console.log('Guardado:', res.data);
          this.form.reset();
          this.esEdicion.emit(false);
          this.registroCompletado.emit(res.data);
        } else {
          this.mensajeError = res.message || 'Ocurrió un error al guardar';
          console.warn('Advertencia:', res.errores || res.message);
        }
        this.enviando = false;
      },
      error: (err) => {
        this.mensajeError = err.error?.mensaje || 'Error inesperado al guardar';
        this.enviando = false;
      }
    });
  }

  editar() {
    this.editando = true;
    this.mensajeError = '';

    this.opticaService.editar(this.optica_id, this.construirOpticaDesdeFormulario()).subscribe({
      next: (resp) => {
        if (resp.success) {
          this.mensajeExito = 'Óptica actualizada correctamente';
          this.editando = false;
          this.mensajeError='';
          setTimeout(() => this.mensajeExito = '', 4000);
        }
      },
      error: (err) => {
        console.error('💥 Error desde backend:', err);
        this.mensajeError = err.error?.message  || 'Error inesperado al editar';
        this.editando = false;
        this.mensajeExito='';
      }
    });
  }

  actualizarPlan() {
    if (this.tienePlanBasico) {
      // solo mostrar tarjeta para upgrade
      this.esEdicion.emit(true);
      this.registroCompletado.emit(this.construirOpticaDesdeFormulario());

    } else {
      // ya tiene premium, mostrar mensaje informativo
      this.mensajeError = 'Ya tenés un plan premium. Podrás cambiarlo cuando finalice esta suscripción.';
      setTimeout(() => this.mensajeError = '', 4000);
    }
  }


  private cargarDatosOptica(optica: Optica): void {
    const provincia = this.listaProvincia.find(p =>
      p.nombre.toLowerCase().trim() === optica.direccion.provincia.toLowerCase().trim()
    );

    if (!provincia) return;

    this.provinciaSeleccionadaId = provincia.id!;
    this.provinciaService.obtenerLocalidades(provincia.id!).subscribe(localidades => {
      this.listaLocalidad = localidades;
      this.localidadesFiltradas = localidades;

      const localidad = localidades.find(l =>
        l.nombre.toLowerCase().trim() === optica.direccion.localidad.toLowerCase().trim()
      );

      this.form.patchValue({
        nombre: optica.nombre,
        email: optica.email,
        telefono: optica.telefono,
        descripcion: optica.descripcion,
        direccion: {
          provincia: provincia.id,
          localidad: localidad?.nombre || '',
          calle: optica.direccion.calle,
          numero: optica.direccion.nroCalle,
          piso: optica.direccion.piso,
          depto: optica.direccion.depto
        }
      });
    });
  }

  private verificarSuscripcion(id_usuario:number): void {
    this.usuarioService.buscarPorUsuario(id_usuario).subscribe({
      next: (suscripcion) => {
        this.tieneSuscripcion = suscripcion.activa;
        if(this.tieneSuscripcion && suscripcion.tipoSuscripcion.id==1){

          this.tienePlanBasico=true;

        }else{
          this.tienePlanBasico=false;
        }
      },
      error: () => {
        this.tieneSuscripcion = false;
      }
    });
  }

  private construirOpticaDesdeFormulario(): Optica {
    const direccionForm = this.form.get('direccion')?.value;
    const provinciaNombre = this.listaProvincia.find(p => p.id === +direccionForm.provincia)?.nombre || '';
    if(this.modoEdicion){
      return {
        id:this.optica_id,
        nombre: this.form.value.nombre,
        email: this.form.value.email,
        telefono: this.form.value.telefono,
        descripcion: this.form.value.descripcion,
        direccion: {
          provincia: provinciaNombre,
          localidad: direccionForm.localidad,
          calle: direccionForm.calle,
          nroCalle: direccionForm.numero,
          piso: direccionForm.piso,
          depto: direccionForm.depto
        },
        logoUrl: this.logoUrlSubido
      };
    }else{
      return {
        nombre: this.form.value.nombre,
        email: this.form.value.email,
        telefono: this.form.value.telefono,
        descripcion: this.form.value.descripcion,
        direccion: {
          provincia: provinciaNombre,
          localidad: direccionForm.localidad,
          calle: direccionForm.calle,
          nroCalle: direccionForm.numero,
          piso: direccionForm.piso,
          depto: direccionForm.depto
        },
        logoUrl: this.logoUrlSubido
      };
    }

  }

  abrirConfirmacion() {
    this.mostrarConfirmacion = true;
  }

  cancelarConfirmacion() {
    this.mostrarConfirmacion = false;
  }

  confirmarAccion() {
    this.mostrarConfirmacion = false;
    this.editar();
  }
}
