package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by Bryn on 4/12/2018.
 */
public class SignatureOutputTests {

    private static final String CQL_TEST_FILE = "SignatureTests/SignatureOutputTests.cql";
    private Map<String, ExpressionDef> defs;

    private Library getLibrary(LibraryBuilder.SignatureLevel signatureLevel) throws IOException {
        final CqlTranslator translator = getTranslator(signatureLevel);
        assertThat(translator.getErrors().size(), is(0));
        defs = new HashMap<>();
        Library library = translator.toELM();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }
        return library;
    }


    private static CqlTranslator getTranslator(LibraryBuilder.SignatureLevel signatureLevel) throws IOException {
        return TestUtils.getTranslator(CQL_TEST_FILE, null, signatureLevel);
    }

    @Test
    public void TestNone() throws IOException {
        final CqlTranslator translator = getTranslator(LibraryBuilder.SignatureLevel.None);
        assertThat(translator.getErrors().size(), greaterThan(1));
        assertThat(translator.getErrors().get(0).getMessage(), equalTo("Please consider setting your compiler signature level to a setting other than None:  Ambiguous forward function declaration for function name: MultipleOverloadTest"));
    }

    @Test
    public void TestDiffering() throws IOException {
        Library library = getLibrary(LibraryBuilder.SignatureLevel.Differing);
        // TestAvg->Avg->signature(1)
        // TestDivide->Divide->signature(2)
        // TestIntegerOverload->OverloadTest->signature(1)
        ExpressionDef def = defs.get("TestAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDateAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDateTime");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestAvg");
        assertThat(((AggregateExpression)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDivide");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestIntegerOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDecimalOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerAndDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));
    }

    @Test
    public void TestOverloads() throws IOException {
        Library library = getLibrary(LibraryBuilder.SignatureLevel.Overloads);
        // TestAdd->operand->signature(2)
        // TestDateAdd->operand->signature(2)
        // TestAvg->Avg->signature(1)
        // TestDivide->Divide->signature(2)
        // TestIntegerMultipleOverload->MultipleOverloadTest->signature(1)
        // TestDecimalMultipleOverload->MultipleOverloadTest->signature(2)
        ExpressionDef def = defs.get("TestAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestDateAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestDateTime");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestAvg");
        assertThat(((AggregateExpression)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDivide");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestIntegerOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDecimalOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestIntegerAndDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));
    }

    @Test
    public void TestAll() throws IOException {
        Library library = getLibrary(LibraryBuilder.SignatureLevel.All);
        // TestAdd->operand->signature(2)
        // TestDateAdd->operand->signature(2)
        // TestDateTime->DateTime->signature(3)
        // TestAvg->Avg->signature(1)
        // TestDivide->Divide->signature(2)
        // TestIntegerOverload->OverloadTest->signature(1)
        // TestDecimalOverload->OverloadTest->signature(1)
        // TestIntegerMultipleOverload->MultipleOverloadTest->signature(1)
        // TestDecimalMultipleOverload->MultipleOverloadTest->signature(2)
        ExpressionDef def = defs.get("TestAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestDateAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestDateTime");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(3));

        def = defs.get("TestAvg");
        assertThat(((AggregateExpression)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDivide");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestIntegerOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDecimalOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestIntegerAndDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(3));
    }

}
