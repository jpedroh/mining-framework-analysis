# coding: utf-8

"""
    openapi 3.0.3 sample spec

    sample spec for testing openapi functionality, built from json schema tests for draft6  # noqa: E501

    The version of the OpenAPI document: 0.0.1
    Generated by: https://openapi-generator.tech
"""

from unit_test_api.paths.request_body_post_nested_oneof_to_check_validation_semantics_request_body.post import PostNestedOneofToCheckValidationSemanticsRequestBody
from unit_test_api.paths.response_body_post_nested_oneof_to_check_validation_semantics_response_body_for_content_types.post import PostNestedOneofToCheckValidationSemanticsResponseBodyForContentTypes
from unit_test_api.paths.request_body_post_oneof_complex_types_request_body.post import PostOneofComplexTypesRequestBody
from unit_test_api.paths.response_body_post_oneof_complex_types_response_body_for_content_types.post import PostOneofComplexTypesResponseBodyForContentTypes
from unit_test_api.paths.request_body_post_oneof_request_body.post import PostOneofRequestBody
from unit_test_api.paths.response_body_post_oneof_response_body_for_content_types.post import PostOneofResponseBodyForContentTypes
from unit_test_api.paths.request_body_post_oneof_with_base_schema_request_body.post import PostOneofWithBaseSchemaRequestBody
from unit_test_api.paths.response_body_post_oneof_with_base_schema_response_body_for_content_types.post import PostOneofWithBaseSchemaResponseBodyForContentTypes
from unit_test_api.paths.request_body_post_oneof_with_empty_schema_request_body.post import PostOneofWithEmptySchemaRequestBody
from unit_test_api.paths.response_body_post_oneof_with_empty_schema_response_body_for_content_types.post import PostOneofWithEmptySchemaResponseBodyForContentTypes
from unit_test_api.paths.request_body_post_oneof_with_required_request_body.post import PostOneofWithRequiredRequestBody
from unit_test_api.paths.response_body_post_oneof_with_required_response_body_for_content_types.post import PostOneofWithRequiredResponseBodyForContentTypes


class OneOfApi(
    PostNestedOneofToCheckValidationSemanticsRequestBody,
    PostNestedOneofToCheckValidationSemanticsResponseBodyForContentTypes,
    PostOneofComplexTypesRequestBody,
    PostOneofComplexTypesResponseBodyForContentTypes,
    PostOneofRequestBody,
    PostOneofResponseBodyForContentTypes,
    PostOneofWithBaseSchemaRequestBody,
    PostOneofWithBaseSchemaResponseBodyForContentTypes,
    PostOneofWithEmptySchemaRequestBody,
    PostOneofWithEmptySchemaResponseBodyForContentTypes,
    PostOneofWithRequiredRequestBody,
    PostOneofWithRequiredResponseBodyForContentTypes,
):
    """NOTE: This class is auto generated by OpenAPI Generator
    Ref: https://openapi-generator.tech

    Do not edit the class manually.
    """
    pass
