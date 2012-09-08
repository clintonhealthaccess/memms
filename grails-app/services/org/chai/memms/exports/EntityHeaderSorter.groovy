package org.chai.memms.exports;

import java.lang.reflect.Field;
import java.util.Comparator;

public class EntityHeaderSorter {

	public static final Comparator<Field> BY_FIELD() {
		return new Comparator<Field>() {
			public int compare(Field field1, Field field2) {
				if (field1 == null || field2 == null)
					return 0;
				
				if (field1.getName().toLowerCase() == "code")
					return -1;
				if (field1.getName().toLowerCase() == "names"
						&& field2.getName().toLowerCase() != "code")
					return -1;
				if (field1.getName().toLowerCase() == "order"
						&& field2.getName().toLowerCase() != "code"
						&& field2.getName().toLowerCase() != "names")
					return -1;
				
				if (field2.getName().toLowerCase() == "code")
					return 1;
				if (field2.getName().toLowerCase() == "names"
						&& field1.getName().toLowerCase() != "code")
					return 1;
				if (field2.getName().toLowerCase() == "order"
						&& field1.getName().toLowerCase() != "code"
						&& field1.getName().toLowerCase() != "names")
					return 1;

				else
					return 0;
			}
		};
	}
}
