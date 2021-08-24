/* tslint:disable */
/* eslint-disable */
/**
 * OpenAPI Petstore
 * This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: \" \\
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { exists, mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface EnumArrays
 */
export interface EnumArrays {
    /**
     * 
     * @type {string}
     * @memberof EnumArrays
     */
    justSymbol?: EnumArraysJustSymbolEnum;
    /**
     * 
     * @type {Array<string>}
     * @memberof EnumArrays
     */
    arrayEnum?: Array<EnumArraysArrayEnumEnum>;
}

/**
* @export
* @enum {string}
*/
export enum EnumArraysJustSymbolEnum {
    GreaterThanOrEqualTo = '>=',
    Dollar = '$'
}/**
* @export
* @enum {string}
*/
export enum EnumArraysArrayEnumEnum {
    Fish = 'fish',
    Crab = 'crab'
}

export function EnumArraysFromJSON(json: any): EnumArrays {
    return EnumArraysFromJSONTyped(json, false);
}

export function EnumArraysFromJSONTyped(json: any, ignoreDiscriminator: boolean): EnumArrays {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'justSymbol': !exists(json, 'just_symbol') ? undefined : json['just_symbol'],
        'arrayEnum': !exists(json, 'array_enum') ? undefined : json['array_enum'],
    };
}

export function EnumArraysToJSON(value?: EnumArrays | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'just_symbol': value.justSymbol,
        'array_enum': value.arrayEnum,
    };
}

