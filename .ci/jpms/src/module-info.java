// A consumer on the module path: it reads assertj-validation as a named module, and opens its own package to the
// validation provider, which reads the constraints of its beans reflectively.
module consumer {
    requires com.github.jinahya.assertj.validation;
    requires jakarta.validation;
    requires org.assertj.core;
    opens consumer to org.hibernate.validator;
}
