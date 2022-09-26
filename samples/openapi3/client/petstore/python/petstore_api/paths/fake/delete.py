# coding: utf-8

"""


    Generated by: https://openapi-generator.tech
"""

from dataclasses import dataclass
import typing_extensions
import urllib3
from urllib3._collections import HTTPHeaderDict

from petstore_api import api_client, exceptions
from datetime import date, datetime  # noqa: F401
import decimal  # noqa: F401
import functools  # noqa: F401
import io  # noqa: F401
import re  # noqa: F401
import typing  # noqa: F401
import typing_extensions  # noqa: F401
import uuid  # noqa: F401

import frozendict  # noqa: F401

from petstore_api import schemas  # noqa: F401

from . import path

# query params
RequiredStringGroupSchema = schemas.IntSchema
RequiredInt64GroupSchema = schemas.Int64Schema
StringGroupSchema = schemas.IntSchema
Int64GroupSchema = schemas.Int64Schema
RequestRequiredQueryParams = typing_extensions.TypedDict(
    'RequestRequiredQueryParams',
    {
        'required_string_group': typing.Union[RequiredStringGroupSchema, decimal.Decimal, int, ],
        'required_int64_group': typing.Union[RequiredInt64GroupSchema, decimal.Decimal, int, ],
    }
)
RequestOptionalQueryParams = typing_extensions.TypedDict(
    'RequestOptionalQueryParams',
    {
        'string_group': typing.Union[StringGroupSchema, decimal.Decimal, int, ],
        'int64_group': typing.Union[Int64GroupSchema, decimal.Decimal, int, ],
    },
    total=False
)


class RequestQueryParams(RequestRequiredQueryParams, RequestOptionalQueryParams):
    pass


request_query_required_string_group = api_client.QueryParameter(
    name="required_string_group",
    style=api_client.ParameterStyle.FORM,
    schema=RequiredStringGroupSchema,
    required=True,
    explode=True,
)
request_query_required_int64_group = api_client.QueryParameter(
    name="required_int64_group",
    style=api_client.ParameterStyle.FORM,
    schema=RequiredInt64GroupSchema,
    required=True,
    explode=True,
)
request_query_string_group = api_client.QueryParameter(
    name="string_group",
    style=api_client.ParameterStyle.FORM,
    schema=StringGroupSchema,
    explode=True,
)
request_query_int64_group = api_client.QueryParameter(
    name="int64_group",
    style=api_client.ParameterStyle.FORM,
    schema=Int64GroupSchema,
    explode=True,
)
# header params
RequiredBooleanGroupSchema = schemas.BoolSchema
BooleanGroupSchema = schemas.BoolSchema
RequestRequiredHeaderParams = typing_extensions.TypedDict(
    'RequestRequiredHeaderParams',
    {
        'required_boolean_group': typing.Union[RequiredBooleanGroupSchema, bool, ],
    }
)
RequestOptionalHeaderParams = typing_extensions.TypedDict(
    'RequestOptionalHeaderParams',
    {
        'boolean_group': typing.Union[BooleanGroupSchema, bool, ],
    },
    total=False
)


class RequestHeaderParams(RequestRequiredHeaderParams, RequestOptionalHeaderParams):
    pass


request_header_required_boolean_group = api_client.HeaderParameter(
    name="required_boolean_group",
    style=api_client.ParameterStyle.SIMPLE,
    schema=RequiredBooleanGroupSchema,
    required=True,
)
request_header_boolean_group = api_client.HeaderParameter(
    name="boolean_group",
    style=api_client.ParameterStyle.SIMPLE,
    schema=BooleanGroupSchema,
)
_auth = [
    'bearer_test',
]


@dataclass
class ApiResponseFor400(api_client.ApiResponse):
    response: urllib3.HTTPResponse
    body: schemas.Unset = schemas.unset
    headers: schemas.Unset = schemas.unset


_response_for_400 = api_client.OpenApiResponse(
    response_cls=ApiResponseFor400,
)
_status_code_to_response = {
    '400': _response_for_400,
}


class BaseApi(api_client.Api):

    def _group_parameters_oapg(
        self: api_client.Api,
        query_params: RequestQueryParams = frozendict.frozendict(),
        header_params: RequestHeaderParams = frozendict.frozendict(),
        stream: bool = False,
        timeout: typing.Optional[typing.Union[int, typing.Tuple]] = None,
        skip_deserialization: bool = False,
    ) -> typing.Union[
        api_client.ApiResponseWithoutDeserialization
    ]:
        """
        Fake endpoint to test group parameters (optional)
        :param skip_deserialization: If true then api_response.response will be set but
            api_response.body and api_response.headers will not be deserialized into schema
            class instances
        """
        self._verify_typed_dict_inputs_oapg(RequestQueryParams, query_params)
        self._verify_typed_dict_inputs_oapg(RequestHeaderParams, header_params)
        used_path = path.value

        prefix_separator_iterator = None
        for parameter in (
            request_query_required_string_group,
            request_query_required_int64_group,
            request_query_string_group,
            request_query_int64_group,
        ):
            parameter_data = query_params.get(parameter.name, schemas.unset)
            if parameter_data is schemas.unset:
                continue
            if prefix_separator_iterator is None:
                prefix_separator_iterator = parameter.get_prefix_separator_iterator()
            serialized_data = parameter.serialize(parameter_data, prefix_separator_iterator)
            for serialized_value in serialized_data.values():
                used_path += serialized_value

        _headers = HTTPHeaderDict()
        for parameter in (
            request_header_required_boolean_group,
            request_header_boolean_group,
        ):
            parameter_data = header_params.get(parameter.name, schemas.unset)
            if parameter_data is schemas.unset:
                continue
            serialized_data = parameter.serialize(parameter_data)
            _headers.extend(serialized_data)
        # TODO add cookie handling

        response = self.api_client.call_api(
            resource_path=used_path,
            method='delete'.upper(),
            headers=_headers,
            auth_settings=_auth,
            stream=stream,
            timeout=timeout,
        )

        if skip_deserialization:
            api_response = api_client.ApiResponseWithoutDeserialization(response=response)
        else:
            response_for_status = _status_code_to_response.get(str(response.status))
            if response_for_status:
                api_response = response_for_status.deserialize(response, self.api_client.configuration)
            else:
                api_response = api_client.ApiResponseWithoutDeserialization(response=response)

        if not 200 <= response.status <= 299:
            raise exceptions.ApiException(api_response=api_response)

        return api_response


class GroupParameters(BaseApi):
    # this class is used by api classes that refer to endpoints with operationId fn names

    def group_parameters(
        self: BaseApi,
        query_params: RequestQueryParams = frozendict.frozendict(),
        header_params: RequestHeaderParams = frozendict.frozendict(),
        stream: bool = False,
        timeout: typing.Optional[typing.Union[int, typing.Tuple]] = None,
        skip_deserialization: bool = False,
    ) -> typing.Union[
        api_client.ApiResponseWithoutDeserialization
    ]:
        return self._group_parameters_oapg(
            query_params=query_params,
            header_params=header_params,
            stream=stream,
            timeout=timeout,
            skip_deserialization=skip_deserialization
        )


class ApiFordelete(BaseApi):
    # this class is used by api classes that refer to endpoints by path and http method names

    def delete(
        self: BaseApi,
        query_params: RequestQueryParams = frozendict.frozendict(),
        header_params: RequestHeaderParams = frozendict.frozendict(),
        stream: bool = False,
        timeout: typing.Optional[typing.Union[int, typing.Tuple]] = None,
        skip_deserialization: bool = False,
    ) -> typing.Union[
        api_client.ApiResponseWithoutDeserialization
    ]:
        return self._group_parameters_oapg(
            query_params=query_params,
            header_params=header_params,
            stream=stream,
            timeout=timeout,
            skip_deserialization=skip_deserialization
        )


