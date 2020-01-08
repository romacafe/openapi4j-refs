import org.junit.Test;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.*;

import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class RefsExample {
    @Test
    public void test_refResolve_same$ref_differentContent() throws Exception {
        // What happens if 2 $refs in different files use the same $ref string but point to different content?
        // It fails.  One of the refs will point to the wrong content
        OpenApi3Parser parser = new OpenApi3Parser();
        URL url = this.getClass().getResource("/api.yaml");
        OpenApi3 oas = parser.parse(url, false);
        OAIContext context = oas.getContext();

        Schema schema1 = oas.getComponents().getSchema("Schema1");
        String testType1Ref = schema1.getProperty("testType").getRef();
        Schema testType1 = context.getReferenceRegistry().getRef(testType1Ref).getMappedContent(Schema.class);

        String schema2Ref = oas.getComponents().getSchema("Schema2").getRef();
        Schema schema2 = context.getReferenceRegistry().getRef(schema2Ref).getMappedContent(Schema.class);
        String testType2Ref = schema2.getProperty("testType").getRef();
        Schema testType2 = context.getReferenceRegistry().getRef(testType2Ref).getMappedContent(Schema.class);

        assertThat(testType1.getProperty("id").getType(), is("integer")); // fail
        assertThat(testType2.getProperty("id").getType(), is("string"));
    }
}
