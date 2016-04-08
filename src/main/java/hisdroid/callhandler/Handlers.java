package hisdroid.callhandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import hisdroid.callhandler.CallHandler.MethodSig;
import hisdroid.callhandler.intent.*;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public class Handlers {
	// methodSubSignature -> (superClass -> Handler)
	static Map<String, Map<SootClass, CallHandler>> m = new HashMap<String, Map<SootClass, CallHandler>>();
	
	static {
		insertHandler(new StringInitHandler());
		insertHandler(new GetIntentHandler());
		insertHandler(new IntentGetExtrasHandler());
		insertHandler(new IntentGetStringHandler());
		insertHandler(new IntentGetIntHandler());
	}
	
	static public CallHandler getHandler(SootMethod method){
		Map<SootClass, CallHandler> m2 = m.get(method.getSubSignature());
		if (m2 == null) {
			return null;
		}
		
		for (SootClass c = method.getDeclaringClass(); c != null; c = c.getSuperclass()) {
			CallHandler ch = m2.get(c);
			if (ch != null) {
				return ch;
			}
		}
		return null;
	}
	
	static public void insertHandler(CallHandler handler){
		Set<MethodSig> sigs = handler.getTargets();
		for (MethodSig sig: sigs){
			String methodSubSignature = sig.subSignature;
			SootClass declaringClass = Scene.v().getSootClass(sig.className);
			Map<SootClass, CallHandler> m2 = m.get(methodSubSignature);
			if (m2 == null) {
				m2 = new HashMap<SootClass, CallHandler>();
				m.put(methodSubSignature, m2);
			}
			m2.put(declaringClass, handler);
		}
	}
}
