#ifndef LLP_LAB2_PRINTER_H
#define LLP_LAB2_PRINTER_H

#include "ast.h"
#include "types.h"
#include "stdio.h"

char *node_types[] = {
        [FILENAME] = "filename",
        [INSERT] = "insert",
        [SELECT] = "select",
        [UPDATE] = "update",
        [DELETE] = "delete",
        [INDEX] = "index",
        [NODE] = "node",
        [LINK] = "link",
        [CONDITIONAL] = "conditional",
        [FILTER] = "filter",
        [SET] = "set",
        [VAL] = "val",
        [NAME] = "name",
        [LIST] = "list",
};

char *insert_targets[] = {
        [I_INDEX] = "index",
        [I_LINK] = "link",
        [I_NODE] = "node",
};

char *logical_ops[] = {
        [AND] = "and",
        [OR] = "or",
        [NOT] = "not",
};

char *comparators[] = {
        [GT] = "greater",
        [LT] = "less",
        [EQ] = "equal",
        [GT_EQ] = "greater or equal",
        [LT_EQ] = "less or equal",
        [SUBSTR] = "contains as substring",
};

void print_attr(attr_t *attr, int32_t nesting_level) {
    switch (attr->type) {
        case INT: {
            printf("%*svalue = %d\n", nesting_level, "", attr->val.i);
            break;
        }
        case DOUBLE: {
            printf("%*svalue = %f\n", nesting_level, "", attr->val.d);
            break;
        }
        case BOOL: {
            if (attr->val.b) {
                printf("%*svalue = TRUE\n", nesting_level, "");
            } else {
                printf("%*svalue = FALSE\n", nesting_level, "");
            }
        }
        case STRING: {
            printf("%*svalue = %s\n", nesting_level, "", attr->val.str.str);
            break;
        }
    }
}

void print_value(ast_node *node, int32_t nesting_level) {
    switch (node->type) {
        case FILENAME: {
            printf("%*s%s\n", nesting_level, "", (char *) node->value);
            break;
        }
        case INSERT: {
            printf("%*s%s\n", nesting_level, "", insert_targets[*(insert_target_t *) (node->value)]);
            break;
        }
        case UPDATE: {
            set_t *s = node->value;
            printf("%*snode to update id = %d\n", nesting_level, "", s->node_id);
            printf("%*sattr to update name = %s\n", nesting_level, "", s->attr_name);
            print_attr(&s->new_value, nesting_level);
        }
        case INDEX: {
            if (node->value) {
                printf("%*sname: %s\n", nesting_level, "", (char *) (node->value));
                break;
            }
        }
        case FILTER: {
            printf("%*slogical operator: %s\n", nesting_level, "", logical_ops[*(logical_op *) (node->value)]);
            break;
        }
        case CONDITIONAL: {
            printf("%*scomparator: %s\n", nesting_level, "", comparators[*(cmp_t *) (node->value)]);
            break;
        }
        case LINK: {
            link_t *l = node->value;
            printf("%*slink_id = %d\n", nesting_level, "", l->link_id);
            printf("%*snode_from_type_id = %d, node_from_id = %d\n", nesting_level, "", l->node_from_type_id,
                   l->node_from_id);
            printf("%*snode_to_type_id = %d, node_to_id = %d\n", nesting_level, "", l->node_to_type_id, l->node_to_id);
            break;
        }
        case VAL: {
            print_attr(node->value, nesting_level);
            break;
        }
        case NAME: {
            printf("%*s%s\n", nesting_level, "", (char *) node->value);
            break;
        }
        default:
            printf("%*s%s\n", nesting_level, "", "none");
            break;
    }
}

void print_node(ast_node *node, int32_t nesting_level) {
    if (node) {
        printf("%*sNode: %s\n", nesting_level, "", node_types[node->type]);
        printf("%*sValue:\n", nesting_level, "");
        print_value(node, nesting_level + 2);
        if (node->left) {
            printf("%*sLeft:\n", nesting_level, "");
            print_node(node->left, nesting_level + 4);
        }
        if(node->right) {
            printf("%*sRight:\n", nesting_level, "");
            print_node(node->right, nesting_level + 4);
        }
    } else {
        printf("%*sNone\n", nesting_level, "");
    }
}

#endif