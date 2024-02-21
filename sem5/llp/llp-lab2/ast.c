#include <malloc.h>
#include <stdio.h>
#include "ast.h"

ast_node *create_node() {
    ast_node *node = malloc(sizeof(ast_node));
    *node = (ast_node) {0};
    return node;
}

void free_node(ast_node *node) {
    if (node) {
        free_node(node->left);
        free_node(node->right);
        if (node->value) free(node->value);
        free(node);
    }
}

ast_node *new_filename_node(char *filename, ast_node *query) {
    ast_node *this = create_node();
    this->type = FILENAME;
    this->left = query;
    this->value = filename;
    return this;
}

ast_node *new_insert_node(ast_node *index, insert_body_t obj) {
    ast_node *this = create_node();
    this->type = INSERT;
    this->left = index;
    this->right = obj.body;
    insert_target_t *t = malloc(sizeof(insert_target_t));
    *t = obj.target;
    this->value = t;
    return this;
}

ast_node *new_select_node(ast_node *index, ast_node *body) {
    ast_node *this = create_node();
    this->type = SELECT;
    this->left = index;
    this->right = body;
    return this;
}

ast_node *new_delete_node(ast_node *index, ast_node *body) {
    ast_node *this = create_node();
    this->type = DELETE;
    this->left = index;
    this->right = body;
    return this;
}

ast_node *new_update_node(ast_node *index, set_t *update) {
    ast_node *this = create_node();
    this->type = UPDATE;
    this->left = index;
    this->value = update;
    return this;
}

ast_node *new_index_node_from_name(char index_name[16]) {
    ast_node *this = create_node();
    this->type = INDEX;
    this->value = index_name;
    return this;
}

ast_node *new_index_node_from_attrs(ast_node *attrs_list) {
    ast_node *this = create_node();
    this->type = INDEX;
    this->left = attrs_list;
    return this;
}

ast_node *add_to_list(ast_node *list, ast_node *val) {
    ast_node *this = create_node();
    this->type = LIST;
    this->left = val;
    this->right = list;
    return this;
}

set_t *new_set_expr(uint32_t node_id, char name[16], attr_t *new_val) {
    set_t *this = malloc(sizeof(set_t));
    this->node_id = node_id;
    this->attr_name = name;
    this->new_value = *new_val;
    return this;
}

ast_node *new_filter_node(ast_node *l, ast_node *r, logical_op op) {
    ast_node *this = create_node();
    this->type = FILTER;
    this->left = l;
    this->right = r;
    logical_op *l_op = malloc(sizeof(logical_op));
    *l_op = op;
    this->value = l_op;
    return this;
}

ast_node *new_conditional_node(ast_node *name, cmp_t cmp, ast_node *val) {
    ast_node *this = create_node();
    this->type = CONDITIONAL;
    this->left = name;
    this->right = val;
    cmp_t *c = malloc(sizeof(cmp_t));
    *c = cmp;
    this->value = c;
    return this;
}

ast_node *new_node_node(ast_node *val_list) {
    ast_node *this = create_node();
    this->type = NODE;
    this->left = val_list;
    return this;
}

ast_node *new_link_node(link_t link) {
    ast_node *this = create_node();
    this->type = LINK;
    link_t *l = malloc(sizeof(link_t));
    *l = link;
    this->value = l;
    return this;
}

ast_node *new_val_node(attr_t *attr) {
    ast_node *this = create_node();
    this->type = VAL;
    attr_t *v = malloc(sizeof(attr_t));
    *v = *attr;
    this->value = v;
    return this;
}

attr_t *new_int_attr(int32_t i_val) {
    attr_t *this = malloc(sizeof(attr_t));
    this->type = INT;
    this->val.i = i_val;
    return this;
}

attr_t *new_double_attr(double d_val) {
    attr_t *this = malloc(sizeof(attr_t));
    this->type = DOUBLE;
    this->val.d = d_val;
    return this;
}

attr_t *new_bool_attr(bool b_val) {
    attr_t *this = malloc(sizeof(attr_t));
    this->type = BOOL;
    this->val.b = b_val;
    return this;
}

attr_t *new_str_attr(char *str_val, uint32_t size) {
    attr_t *this = malloc(sizeof(attr_t));
    this->type = STRING;
    this->val.str = (string_t) {.size = size, .str = str_val};
    return this;
}

ast_node *new_name_node(char name[16]) {
    ast_node *this = create_node();
    this->type = NAME;
    this->value = name;
    return this;
}
