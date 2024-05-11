`timescale 1ns / 1ps
`include "adder.v"
`include "cqrt.v"

module cqrt_tb();

reg clk;
parameter PERIOD = 5;
initial begin
    clk = 1;
    forever
        #PERIOD
        clk = ~clk;
end

reg [8:0] i;
integer lower, upper;
wire is_end_i;

reg [7:0] a;
reg start, rst;

wire [3:0] out;
wire out_busy;

wire [7:0] sum_a, sum_b;
wire [8:0] sum_res;

adder sum(
    .a(sum_a),
    .b(sum_b),
    .res(sum_res)
);

cqrt cqrt(
    .clk(clk),
    .rst(rst),
    .a(a),
    .start(start),

    .res(out),
    .busy(out_busy),

    .sum_out(sum_res),
    .sum_a_w(sum_a),
    .sum_b_w(sum_b)
);

assign is_end_i = (i == 256);

localparam PREPARE = 4'd0;
localparam CYCLE_I = 4'd1;
localparam NEXT_I = 4'd3;
localparam BODY = 4'd4;
localparam BODY_1 = 4'd5;
localparam BODY_2 = 4'd6;
localparam BODY_3 = 4'd7;
localparam BODY_4 = 4'd8;


reg [3:0] state = PREPARE;

always @(posedge clk) begin
    case (state)
        PREPARE:
            begin
                i <= 8'd0;
                rst <= 1;
                state <= CYCLE_I;
            end
        CYCLE_I:
            begin
                if (is_end_i) begin
                    $finish;
                end else begin
                    state <= BODY;
                end
            end
        NEXT_I:
            begin
                i <= i + 1;
                state <= CYCLE_I;
            end
        BODY:
            begin
                a <= i;
                start <= 1;
                rst <= 0;
                state <= BODY_1;
            end
        BODY_1:
            begin
                start <= 0;
                state <= BODY_2;
            end
        BODY_2:
            begin
                if (out_busy == 0) begin
                    state <= BODY_3;
                end
            end
        BODY_3:
            begin
                lower <= out * out * out;
                upper <= (out + 1) * (out + 1) * (out + 1);
                state <= BODY_4;
            end
        BODY_4:
            begin
                if (lower <= i && upper > i) begin
                    $display("Correct! a = %0d, y = %0d, lower = %0d, upper = %0d", a, out, lower, upper);
                end else begin
                    $display("Incorrect! a = %0d, y = %0d, lower = %0d, upper = %0d", a, out, lower, upper);
                end
                state <= NEXT_I;
            end
    endcase
end

endmodule