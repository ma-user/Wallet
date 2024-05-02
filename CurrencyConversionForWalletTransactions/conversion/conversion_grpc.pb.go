// Code generated by protoc-gen-go-grpc. DO NOT EDIT.
// versions:
// - protoc-gen-go-grpc v1.2.0
// - protoc             v4.25.3
// source: conversion/conversion.proto

package currency_conversion

import (
	context "context"
	grpc "google.golang.org/grpc"
	codes "google.golang.org/grpc/codes"
	status "google.golang.org/grpc/status"
)

// This is a compile-time assertion to ensure that this generated file
// is compatible with the grpc package it is being compiled against.
// Requires gRPC-Go v1.32.0 or later.
const _ = grpc.SupportPackageIsVersion7

// ConversionServiceClient is the client API for ConversionService service.
//
// For semantics around ctx use and closing/ending streaming RPCs, please refer to https://pkg.go.dev/google.golang.org/grpc/?tab=doc#ClientConn.NewStream.
type ConversionServiceClient interface {
	ConvertCurrency(ctx context.Context, in *ConversionRequest, opts ...grpc.CallOption) (*ConversionResponse, error)
}

type conversionServiceClient struct {
	cc grpc.ClientConnInterface
}

func NewConversionServiceClient(cc grpc.ClientConnInterface) ConversionServiceClient {
	return &conversionServiceClient{cc}
}

func (c *conversionServiceClient) ConvertCurrency(ctx context.Context, in *ConversionRequest, opts ...grpc.CallOption) (*ConversionResponse, error) {
	out := new(ConversionResponse)
	err := c.cc.Invoke(ctx, "/conversion.ConversionService/ConvertCurrency", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

// ConversionServiceServer is the server API for ConversionService service.
// All implementations must embed UnimplementedConversionServiceServer
// for forward compatibility
type ConversionServiceServer interface {
	ConvertCurrency(context.Context, *ConversionRequest) (*ConversionResponse, error)
	mustEmbedUnimplementedConversionServiceServer()
}

// UnimplementedConversionServiceServer must be embedded to have forward compatible implementations.
type UnimplementedConversionServiceServer struct {
}

func (UnimplementedConversionServiceServer) ConvertCurrency(context.Context, *ConversionRequest) (*ConversionResponse, error) {
	return nil, status.Errorf(codes.Unimplemented, "method ConvertCurrency not implemented")
}
func (UnimplementedConversionServiceServer) mustEmbedUnimplementedConversionServiceServer() {}

// UnsafeConversionServiceServer may be embedded to opt out of forward compatibility for this service.
// Use of this interface is not recommended, as added methods to ConversionServiceServer will
// result in compilation errors.
type UnsafeConversionServiceServer interface {
	mustEmbedUnimplementedConversionServiceServer()
}

func RegisterConversionServiceServer(s grpc.ServiceRegistrar, srv ConversionServiceServer) {
	s.RegisterService(&ConversionService_ServiceDesc, srv)
}

func _ConversionService_ConvertCurrency_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(ConversionRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(ConversionServiceServer).ConvertCurrency(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/conversion.ConversionService/ConvertCurrency",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(ConversionServiceServer).ConvertCurrency(ctx, req.(*ConversionRequest))
	}
	return interceptor(ctx, in, info, handler)
}

// ConversionService_ServiceDesc is the grpc.ServiceDesc for ConversionService service.
// It's only intended for direct use with grpc.RegisterService,
// and not to be introspected or modified (even as a copy)
var ConversionService_ServiceDesc = grpc.ServiceDesc{
	ServiceName: "conversion.ConversionService",
	HandlerType: (*ConversionServiceServer)(nil),
	Methods: []grpc.MethodDesc{
		{
			MethodName: "ConvertCurrency",
			Handler:    _ConversionService_ConvertCurrency_Handler,
		},
	},
	Streams:  []grpc.StreamDesc{},
	Metadata: "conversion/conversion.proto",
}
