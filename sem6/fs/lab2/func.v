`timescale 1ns / 1ps
`include "adder.v"
`include "cqrt.v"

module func(
    input clk,
    input rst,
    input [7:0] a,
    input [7:0] b,
    input start,

    output reg [15:0] out,
    output wire busy
    );

reg [15:0] sum_a, sum_b;
reg sum_flag = 1;
wire [15:0] sum_a_w, sum_b_w;
wire [15:0] sum_a_ww, sum_b_ww;
wire [15:0] sum_out_w;
assign sum_a_ww = ~sum_flag ? sum_a : sum_a_w;
assign sum_b_ww = ~sum_flag ? sum_b : sum_b_w;

adder sum(
    .a(sum_a_ww),
    .b(sum_b_ww),
    .res(sum_out_w)
);

reg [7:0] cqrt_a;
wire [3:0] cqrt_out;
wire cqrt_busy;
reg cqrt_start;
cqrt cqrt(
    .clk(clk),
    .rst(rst),
    .a(cqrt_a),
    .start(cqrt_start),

    .res(cqrt_out),
    .busy(cqrt_busy),

    .sum_out(sum_out_w),
    .sum_a(sum_a_w),
    .sum_b(sum_b_w)
);


reg [7:0] mul_a, mul_b;
wire [15:0] mul_out;
wire mul_busy;
reg mul_start;
multer mult(
    .clk(clk),
    .rst(rst),
    .a(mul_a),
    .b(mul_b),
    .start(mul_start),
    .busy(mul_busy),
    .y(mul_out)
);

localparam IDLE = 0,
           WORK = 1,
           WORK_1 = 2,
           WORK_2 = 3
           ;

reg [4:0] state;

assign busy = (state != IDLE);

always @(posedge clk)
    if (rst) begin
        out <= 0;
        state <= IDLE;
    end else begin
        case (state)
            IDLE :
                if (start) begin
                    mul_a <= a;
                    mul_b <= 3;
                    sum_flag <= 1;
                    cqrt_a <= b;
                    mul_start <= 1;
                    cqrt_start <= 1;
                   //$display("IDLE, mul_a = %d, mul_b = %d, mul_out = %d, cqrt_a = %d,cqrt_out = %d, busy = %d", mul_a, mul_b, mul_out, cqrt_a, cqrt_out, busy);
                    state <= WORK;
                end
            WORK:
                begin
                //$display("WORK, mul_a = %d, mul_b = %d, mul_out = %d, cqrt_a = %d,cqrt_out = %d, busy = %d", mul_a, mul_b, mul_out, cqrt_a, cqrt_out, busy);
                    mul_start <= 0;
                    cqrt_start <= 0;

                    state <= WORK_1;
                end
            WORK_1:
                begin
                //$display("WORK_1, mul_a = %d, mul_b = %d, mul_out = %d, cqrt_a = %d,cqrt_out = %d, busy = %d, mul_b = %d, cqrt_b = %d", mul_a, mul_b, mul_out, cqrt_a, cqrt_out, busy, mul_busy, cqrt_busy);
                    if (~mul_busy && ~cqrt_busy) begin
                        sum_a <= mul_out;
                        sum_b <= cqrt_out << 1;
                        sum_flag <= 0;
                        state <= WORK_2;
                    end
                end
            WORK_2:
                begin
                //$display("WORK_2, mul_a = %d, mul_b = %d, mul_out = %d, cqrt_a = %d,cqrt_out = %d, busy = %d", mul_a, mul_b, mul_out, cqrt_a, cqrt_out, busy);
                    out <= sum_out_w;

                    state <= IDLE;
                end
        endcase
    end
endmodule
