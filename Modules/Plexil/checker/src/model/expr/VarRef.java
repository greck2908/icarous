/* Copyright (c) 2006-2015, Universities Space Research Association (USRA).
*  All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the Universities Space Research Association nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY USRA ``AS IS'' AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL USRA BE LIABLE FOR ANY DIRECT, INDIRECT,
* INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
* BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
* OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
* TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
* USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package model.expr;

import java.util.Vector;

import main.Log;
import model.GlobalDeclList;
import model.Node;
import model.Var;
import model.Var.VarMod;

public class VarRef
    extends Expr {

    protected String id;
    protected ExprType type;
    protected Var decl;

    public VarRef(ExprType t, String name, Var dec) {
        super();
        id = name;
        decl = dec;
        if (t == ExprType.GenericArray
            && dec != null
            && dec.getType().isArrayType())
            t = dec.getType(); // believe array declaration
        type = t; // believe reference
    }

    // For node variable references only
    public VarRef(ExprType t) {
        super();
        type = t;
        id = null;
        decl = null;
    }

    public String getID() {
        return id;
    }

    public ExprType getType() {
        return type;
    }

    public Var getVarDecl() {
        return decl;
    }

    @Override
    public boolean isAssignable() {
        if (decl == null)
            return true; // no way to tell, so assume yes
        return decl.getMod() != VarMod.In;
    }

    public String toString() {
        StringBuilder s = new StringBuilder("(Var ");
        s.append(type.toString());
        s.append(" ");
        s.append(id);
        s.append(")");
        return s.toString();
    }

    /**
     * @brief Check the expression for type and other errors.
     * @param n The node providing the variable binding context.
     * @param decls The plan's global decls.
     * @param contextMsg String to append to any error messages generated.
     * @param errors (in/out parameter) Collection of errors recorded.
     */
    public ExprType check(Node n,
                          GlobalDeclList decls,
                          String contextMsg,
                          Vector<Log> errors) {
        if (decl == null) {
            errors.add(Log.error("Variable " + id + " is referenced but not defined",
                                 contextMsg));
        }
        else {
            if (type == ExprType.GenericArray) {
                if (decl.getType().isArrayType())
                    type = decl.getType();
                else
                    errors.add(Log.error("Variable " + id
                                         + " is declared " + decl.getType()
                                         + ", but reference is " + type,
                                         contextMsg));
            }
            else if (decl.getType() != type) {
                errors.add(Log.error("Variable " + id
                                     + " is declared " + decl.getType()
                                     + ", but reference is " + type,
                                     contextMsg));
            }
        }
        return type;
    }
}
