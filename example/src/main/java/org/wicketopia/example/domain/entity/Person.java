/*
 * Copyright (c) 2011 Carman Consulting, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wicketopia.example.domain.entity;

import org.domdrides.entity.UuidEntity;
import org.wicketopia.builder.feature.annotation.metadata.DisplayName;
import org.wicketopia.builder.feature.annotation.metadata.Order;
import org.wicketopia.builder.feature.annotation.required.Required;
import org.wicketopia.builder.feature.annotation.validator.Email;
import org.wicketopia.builder.feature.annotation.validator.Pattern;
import org.wicketopia.example.domain.value.Gender;
import org.wicketopia.spring.security.annotation.VisibleForRole;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Person extends UuidEntity
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private String firstName;
    private String lastName;
    private String ssn;
    private String email;
    private Gender gender;
    private Date dob;
    
    private boolean smoker;

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public Date getDob()
    {
        return dob;
    }

    public void setDob(Date dob)
    {
        this.dob = dob;
    }

    @Email
    @Order(3)
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Order(1)
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public Gender getGender()
    {
        return gender;
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
    }

    @Required
    @Order(2)
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    @DisplayName("SSN")
    @Order(4)
    @Pattern("^\\d{3}[- ]?\\d{2}[- ]?\\d{4}$")
    @VisibleForRole("ROLE_ADMIN")
    public String getSsn()
    {
        return ssn;
    }

    public void setSsn(String ssn)
    {
        this.ssn = ssn;
    }

    public boolean isSmoker()
    {
        return smoker;
    }

    public void setSmoker(boolean smoker)
    {
        this.smoker = smoker;
    }
}
