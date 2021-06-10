import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { HddpPage } from './hddp.page';

describe('HddpPage', () => {
  let component: HddpPage;
  let fixture: ComponentFixture<HddpPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HddpPage ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(HddpPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
