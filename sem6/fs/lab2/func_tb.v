`timescale 1ns / 1ps
`include "func.v"

module func_tb();

reg clk;
parameter PERIOD = 5;
initial begin
    clk = 1;
    forever
        #PERIOD
        clk = ~clk;
end


reg [8:0] i, j;
reg [10:0] triple_i;
reg [10:0] test_cqrt;
reg [10:0] lower, upper;
wire is_end_i, is_end_j;

reg [7:0] in_a, in_b;
reg start, in_rst;

wire [9:0] out;
wire out_busy;

func func(
    .clk(clk),
    .rst(in_rst),
    .a(in_a),
    .b(in_b),
    .start(start),
    .busy(out_busy),
    .out(out)
);

assign is_end_i = (i == 256);
assign is_end_j = (j == 256);

localparam PREPARE = 4'd0;
localparam CYCLE_I = 4'd1;
localparam CYCLE_J = 4'd2;
localparam NEXT_J = 4'd3;
localparam BODY = 4'd4;
localparam BODY_1 = 4'd5;
localparam BODY_2 = 4'd6;
localparam BODY_3 = 4'd7;
localparam BODY_4 = 4'd8;
localparam BODY_5 = 4'd9;


reg [4:0] state = PREPARE;

always @(posedge clk) begin
    case (state)
        PREPARE:
            begin
                i <= 8'd0;
                j <= 8'd0;
                in_rst <= 1;
                state <= CYCLE_J;
            end
        CYCLE_I:
            begin
                if (is_end_i) begin
                    $finish;
                end else begin
                    j <= 0;
                    state <= CYCLE_J;
                end
            end
        CYCLE_J:
            begin
                if (is_end_j) begin
                    i <= i + 1;
                    state <= CYCLE_I;
                end else begin
                    state <= BODY;
                end
            end
        NEXT_J:
            begin
                j <= j + 1;
                state <= CYCLE_J;
            end
        BODY:
            begin
                in_a <= i;
                in_b <= j;
                triple_i <= i*3;
                start <= 1;
                in_rst <= 0;
                state <= BODY_1;
                //$display("JOPA");
            end
        BODY_1:
            begin
                start <= 0;
                state <= BODY_2;
                //$display("JOPA2");
            end
        BODY_2:
            begin
                if (out_busy == 0) begin
                    state <= BODY_3;
                    //$display("JOP3");
                end
            end
        BODY_3:
            begin
                test_cqrt <= (out - triple_i) >> 1;
                state <= BODY_4;
                //$display("JOPA5");
            end
        BODY_4:
            begin
                lower <= test_cqrt * test_cqrt * test_cqrt;
                upper <= (test_cqrt + 1) * (test_cqrt + 1) * (test_cqrt + 1);

                state <= BODY_5;
                //$display("JOPA6");
            end
        BODY_5:
            begin
                //$display("JOPA7");
                if (lower <= j && upper > j) begin
                    $display("Correct! a_bi = %d, b_bi = %d, y = %d, 3a = %d, lower_cube_j = %d, upper_cube_j = %d, test_cqrt = %d", in_a, in_b, out, triple_i, lower, upper, test_cqrt);
                end else begin
                    $display("Incorrect! a_bi = %d, b_bi = %d, y = %d, 3a = %d, lower_cube_j = %d, upper_cube_j = %d, test_cqrt = %d", in_a, in_b, out, triple_i, lower, upper, test_cqrt);
                end
                state <= NEXT_J;
            end
    endcase
end
endmodule
