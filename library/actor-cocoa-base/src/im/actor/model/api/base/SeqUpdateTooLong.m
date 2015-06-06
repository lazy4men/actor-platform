//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/ex3ndr/Develop/actor-model/library/actor-cocoa-base/build/java/im/actor/model/api/base/SeqUpdateTooLong.java
//


#include "IOSClass.h"
#include "IOSPrimitiveArray.h"
#include "J2ObjC_source.h"
#include "im/actor/model/api/base/SeqUpdateTooLong.h"
#include "im/actor/model/droidkit/bser/Bser.h"
#include "im/actor/model/droidkit/bser/BserObject.h"
#include "im/actor/model/droidkit/bser/BserValues.h"
#include "im/actor/model/droidkit/bser/BserWriter.h"
#include "im/actor/model/network/parser/RpcScope.h"
#include "java/io/IOException.h"

@implementation ImActorModelApiBaseSeqUpdateTooLong

+ (ImActorModelApiBaseSeqUpdateTooLong *)fromBytesWithByteArray:(IOSByteArray *)data {
  return ImActorModelApiBaseSeqUpdateTooLong_fromBytesWithByteArray_(data);
}

- (instancetype)init {
  ImActorModelApiBaseSeqUpdateTooLong_init(self);
  return self;
}

- (void)parseWithBSBserValues:(BSBserValues *)values {
}

- (void)serializeWithBSBserWriter:(BSBserWriter *)writer {
}

- (NSString *)description {
  NSString *res = @"update box SeqUpdateTooLong{";
  res = JreStrcat("$C", res, '}');
  return res;
}

- (jint)getHeaderKey {
  return ImActorModelApiBaseSeqUpdateTooLong_HEADER;
}

@end

ImActorModelApiBaseSeqUpdateTooLong *ImActorModelApiBaseSeqUpdateTooLong_fromBytesWithByteArray_(IOSByteArray *data) {
  ImActorModelApiBaseSeqUpdateTooLong_initialize();
  return ((ImActorModelApiBaseSeqUpdateTooLong *) BSBser_parseWithBSBserObject_withByteArray_(new_ImActorModelApiBaseSeqUpdateTooLong_init(), data));
}

void ImActorModelApiBaseSeqUpdateTooLong_init(ImActorModelApiBaseSeqUpdateTooLong *self) {
  (void) APRpcScope_init(self);
}

ImActorModelApiBaseSeqUpdateTooLong *new_ImActorModelApiBaseSeqUpdateTooLong_init() {
  ImActorModelApiBaseSeqUpdateTooLong *self = [ImActorModelApiBaseSeqUpdateTooLong alloc];
  ImActorModelApiBaseSeqUpdateTooLong_init(self);
  return self;
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(ImActorModelApiBaseSeqUpdateTooLong)
