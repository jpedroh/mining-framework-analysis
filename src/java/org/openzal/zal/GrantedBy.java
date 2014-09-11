/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2014 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openzal.zal;

import org.jetbrains.annotations.NotNull;

/* $if ZimbraVersion >= 8.0.6 $*/
import com.zimbra.soap.admin.type.GranteeSelector.GranteeBy;
/* $endif$ */

/* $if ZimbraVersion >= 8.0.0 && ZimbraVersion < 8.0.6 $
import com.zimbra.common.account.Key.GranteeBy;
   $endif$ */

/* $if ZimbraVersion < 8.0.0 $
import com.zimbra.cs.account.Provisioning.GranteeBy;
   $endif$ */

public class GrantedBy
{
  @NotNull private final GranteeBy mGranteeBy;

  public static GrantedBy id   = new GrantedBy(GranteeBy.id);
  public static GrantedBy name = new GrantedBy(GranteeBy.name);

  public GrantedBy(Object granteeBy)
  {
    mGranteeBy = (GranteeBy) granteeBy;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mGranteeBy);
  }
}
