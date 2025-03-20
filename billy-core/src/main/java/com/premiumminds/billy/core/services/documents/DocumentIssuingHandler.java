/*
 * Copyright (C) 2017 Premium Minds.
 *
 * This file is part of billy core.
 *
 * billy core is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * billy core is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with billy core. If not, see <http://www.gnu.org/licenses/>.
 */
package com.premiumminds.billy.core.services.documents;

import com.premiumminds.billy.core.exceptions.SeriesUniqueCodeNotFilled;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.core.services.exceptions.DocumentSeriesDoesNotExistException;

public interface DocumentIssuingHandler<T extends GenericInvoice, P extends IssuingParams> {

<<<<<<< /usr/src/app/output/premium-minds/billy/9420e73394b22e375a57d57822bad925e7f9572a/billy-core/src/main/java/com/premiumminds/billy/core/services/documents/DocumentIssuingHandler.java/left.java
    public T issue(T document, P parameters) throws DocumentIssuingException, SeriesUniqueCodeNotFilled;
||||||| /usr/src/app/output/premium-minds/billy/9420e73394b22e375a57d57822bad925e7f9572a/billy-core/src/main/java/com/premiumminds/billy/core/services/documents/DocumentIssuingHandler.java/base.java
    public T issue(T document, P parameters) throws DocumentIssuingException;
=======
    public T issue(T document, P parameters) throws DocumentIssuingException, DocumentSeriesDoesNotExistException;
>>>>>>> /usr/src/app/output/premium-minds/billy/9420e73394b22e375a57d57822bad925e7f9572a/billy-core/src/main/java/com/premiumminds/billy/core/services/documents/DocumentIssuingHandler.java/right.java
}
