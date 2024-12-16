/**
 * Copyright (C) 2011 Akiban Technologies Inc.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */
package com.akiban.sql.compiler;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.*;
import com.akiban.sql.types.DataTypeDescriptor;
import com.akiban.sql.types.TypeId;
import java.sql.Types;


public class IntervalTypeCompiler extends TypeCompiler {
    protected IntervalTypeCompiler(TypeId typeId) {
        super(typeId);
    }

    /**
     * Determine if this type (interval) can be converted to some other type.
     */
    public boolean convertible(TypeId otherType, boolean forDataTypeFunction) {
        if (otherType.isStringTypeId() && !otherType.isLongConcatableTypeId()) {
            return true;
        }
        return (getStoredFormatIdFromTypeId() == otherType.getTypeFormatId());
    }

    /**
     * Tell whether this type (interval) is compatible with the given type.
     *
     * @param otherType         The TypeId of the other type.
     */
    public boolean compatible(TypeId otherType) {
        return convertible(otherType, false);
    }

    /**
     * @see TypeCompiler#getCorrespondingPrimitiveTypeName
     */

    public String getCorrespondingPrimitiveTypeName() {
        return null;
    }

    /**
     * Get the method name for getting out the corresponding primitive
     * Java type.
     *
     * @return String The method call name for getting the
     *                              corresponding primitive Java type.
     */
    public String getPrimitiveMethodName() {
        return null;
    }

    /**
     * @see TypeCompiler#getCastToCharWidth
     */
    public int getCastToCharWidth(DataTypeDescriptor dtd) {
        TypeId typeId = dtd.getTypeId();
        if (typeId == TypeId.INTERVAL_YEAR_ID) {
            return dtd.getPrecision(); // yyyy
        }
        else if (typeId == TypeId.INTERVAL_MONTH_ID) {
            return dtd.getPrecision(); // mmmm
        }
        else if (typeId == TypeId.INTERVAL_YEAR_MONTH_ID) {
            return dtd.getPrecision() + 1 + 2; // yyyy-mm
        }
        else if (typeId == TypeId.INTERVAL_DAY_ID) {
            return dtd.getPrecision(); // dddd
        }
        else if (typeId == TypeId.INTERVAL_HOUR_ID) {
            return dtd.getPrecision(); // hhhh
        }
        else if (typeId == TypeId.INTERVAL_MINUTE_ID) {
            return dtd.getPrecision(); // mmmm
        }
        else if (typeId == TypeId.INTERVAL_SECOND_ID) {
            if (dtd.getScale() > 0)
                return dtd.getPrecision() + 1 + dtd.getScale(); // ssss.SSSS
            else
                return dtd.getPrecision(); // ssss
        }
        else if (typeId == TypeId.INTERVAL_DAY_HOUR_ID) {
            return dtd.getPrecision() + 1 + 2; // dddd hh
        }
        else if (typeId == TypeId.INTERVAL_DAY_MINUTE_ID) {
            return dtd.getPrecision() + 1 + 2 + 1 + 2; // dddd hh:mm
        }
        else if (typeId == TypeId.INTERVAL_DAY_SECOND_ID) {
            if (dtd.getScale() > 0)
                return dtd.getPrecision() + 1 + 2 + 1 + 2 + 1 + 2 + 1 + dtd.getScale(); // dd hh:mm:ss.SSSS
            else
                return dtd.getPrecision() + 1 + 2 + 1 + 2 + 1 + 2; // dd hh:mm:ss
        }
        else if (typeId == TypeId.INTERVAL_HOUR_MINUTE_ID) {
            return dtd.getPrecision() + 1 + 2; // hhhh:mm
        }
        else if (typeId == TypeId.INTERVAL_HOUR_SECOND_ID) {
            if (dtd.getScale() > 0)
                return dtd.getPrecision() + 1 + 2 + 1 + 2 + 1 + dtd.getScale(); // hhhh:mm:ss.SSSS
            else
                return dtd.getPrecision() + 1 + 2 + 1 + 2; // hhhh:mm:ss
        }
        else if (typeId == TypeId.INTERVAL_MINUTE_SECOND_ID) {
            if (dtd.getScale() > 0)
                return dtd.getPrecision() + 1 + 2 + 1 + dtd.getScale(); // mmmm:ss.SSSS
            else
                return dtd.getPrecision() + 1 + 2; // mmmm:ss
        }
        assert false : "unexpected typeId in getCastToCharWidth() - " + typeId;
        return 0;
    }

    /**
     * @see TypeCompiler#resolveArithmeticOperation
     *
     * @exception StandardException     Thrown on error
     */
    public DataTypeDescriptor resolveArithmeticOperation(DataTypeDescriptor leftType, DataTypeDescriptor rightType, String operator) throws StandardException {
        TypeId rightTypeId = rightType.getTypeId();
        TypeId leftTypeId = leftType.getTypeId();
        boolean nullable = leftType.isNullable() || rightType.isNullable();
        if (operator.equals(PLUS_OP) || operator.equals(MINUS_OP)) {
            // date/time and interval
            TypeId datetimeType;
            // Let specific datetime type resolve it.
            if (((datetimeType = rightTypeId).isDateTimeTimeStampTypeId() && leftTypeId.isIntervalTypeId()) || ((datetimeType = leftTypeId).isDateTimeTimeStampTypeID() && rightTypeId.isIntervalTypeId())) {
                return getTypeCompiler(datetimeType).resolveArithmeticOperation(rightType, leftType, operator);
            }
            // interval and interval
            int typeFormatId = 0;
                // two intervals are exactly the same
            if (leftTypeId.isIntervalTypeId() && rightTypeId.isIntervalTypeId()) {
                if (leftTypeId == rightTypeId) {
                    return leftType.getNullabilityType(nullable);
                } else if ((typeFormatId = leftTypeId.getTypeFormatId()) == rightTypeId.getTypeFormatId()) {
                    return new DataTypeDescriptor(typeFormatId == TypeId.FormatIds.INTERVAL_DAY_SECOND_ID ? TypeId.INTERVAL_SECOND_ID : TypeId.INTERVAL_MONTH_ID, nullable);
                }
            }
            // varchar
            DataTypeDescriptor varcharType;
            // when left is interval, only + is legal
            if (((varcharType = leftType).getTypeId().isStringTypeId() && rightTypeId.isIntervalTypeId()) || (((varcharType = rightType).getTypeId().isStringTypeId() && leftTypeId.isIntervalTypeId()) && operator.equals(PLUS_OP))) {
                return new DataTypeDescriptor(varcharType.getPrecision() > 10 ? TypeId.DATETIME_ID : TypeId.DATE_ID, nullable);
            }
        } else if (operator.equals(TIMES_OP) || operator.equals(DIVIDE_OP)) {
            // numeric / varchar and interval
            TypeId intervalId = null;
                    // when right is interval, only * is legal
            if (((intervalId = leftTypeId).isIntervalTypeId() && (rightTypeId.isNumericTypeId() || rightTypeId.isStringTypeId())) || (((intervalId = rightTypeId).isIntervalTypeId() && (leftTypeId.isNumericTypeId() || leftTypeId.isStringTypeId())) && operator.equals(TIMES_OP))) {
                return new DataTypeDescriptor(intervalId, nullable);
            }
        }
        // Unsupported
        return super.resolveArithmeticOperation(leftType, rightType, operator);
    }
}