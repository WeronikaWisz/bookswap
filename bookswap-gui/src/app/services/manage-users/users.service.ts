import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {ProfileData} from "../../models/auth/ProfileData";
import {HttpClient} from "@angular/common/http";

const USERS_API = 'http://localhost:8080/users/';

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  constructor(private http: HttpClient) { }

  getUserProfileData(): Observable<ProfileData>{
    return this.http.get<ProfileData>(USERS_API + 'user');
  }
}
