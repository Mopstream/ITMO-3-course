`timescale 1ns / 1ps
`include "multer.v"

module cqrt(
    input clk,
    input rst,
    input [7:0] a,
    input start,

    output reg [3:0] res,
    output wire busy,

    input wire [8:0] sum_out,
    output reg [7:0] sum_a,
    output reg [7:0] sum_b
    );

// always @(posedge clk) begin
//     sum_a <= 5;
//     sum_b <=10;
//     @(posedge clk);
//     $display("a = %d, a_w = %d, b = %d, b_w = %d, out = %d, out_w = %d", sum_a, sum_a, sum_b, sum_b, sum_out, sum_out);
// end

    reg [7:0] mult_a, mult_b;
    wire [15:0] mult_y;
    reg mult_start;
    wire mult_busy;
    multer mult(
        .clk(clk),
        .rst(rst),
        .a(mult_a),
        .b(mult_b),
        .start(mult_start),
        .busy(mult_busy),
        .y(mult_y)
    );

    localparam  IDLE = 0,
                WORK = 1,
                MULT_PREP_1 = 2,
                MULT_PREP_2 = 3,
                MULT_PREP_3 = 4,
                MULT_START_1 = 5,
                MULT_START_2 = 6,
                WAIT_MUL = 7,
                CALC_B = 8,
                CHECK_X = 9,
                SUB_B = 10,
                INC_Y = 11,
                INC_Y_1 = 12;


    reg signed [7:0] s;
    wire end_step;
    reg [7:0] x;
    reg [31:0] b, y, tmp;
    reg [4:0] state;

    assign end_step = (s == 'hfd);
    assign busy = (state != IDLE);

    always@(posedge clk) begin
        if(rst) begin
            state <= IDLE;
        end
        else begin
            case (state)
                IDLE:
                    begin
                    //$display("    IDLE, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        if(start) begin
                        //$display("START");
                            mult_start <= 0;
                            x <= a;
                            s <= 6;
                            y <= 0;
                            state <= WORK;
                        end
                    end

                WORK:
                    begin
                        //$display("    CWORK, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        if(end_step) begin
                            state <= IDLE;
                            res <= y;
                        end else begin
                            //$display("    WWW, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                            y <= y << 1;
                            state <= MULT_PREP_1;
                        end
                    end

                MULT_PREP_1:
                    begin
                        //$display("    MPREP1, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        state <= MULT_PREP_2;
                        tmp <= y << 1;
                    end

                MULT_PREP_2:
                    begin
                        //$display("    MPREP2, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        state <= MULT_PREP_3;
                        sum_a <= tmp;
                        sum_b <= y;
                    end

                MULT_PREP_3:
                    begin
                        //$display("    MPREP3, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        state <= MULT_START_1;
                        tmp <= sum_out;
                        sum_a <= y;
                        sum_b <= 1;
                    end

                MULT_START_1:
                    begin
                        //$display("    MSTART1, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        state <= MULT_START_2;
                        mult_a <= tmp;
                        mult_b <= sum_out;
                        mult_start <= 1;
                    end

                MULT_START_2:
                    begin
                        //$display("    MSTART2, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        mult_start <= 0;
                        state <= WAIT_MUL;
                    end

                WAIT_MUL:
                    begin
                      ///$display("    MWAIT, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        if(~mult_busy) begin
                            state <= CALC_B;
                            sum_a <= mult_y;
                            sum_b <= 1;
                        end
                    end

                CALC_B:
                    begin
                        //$display("    CALC, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        state <= CHECK_X;
                        b <= sum_out << s;
                        sum_a <= s;
                        sum_b <= -3;
                    end

                CHECK_X:
                    begin
                        //$display("    CHECK, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        if(x >= b) begin
                            state <= SUB_B;
                            sum_a <= ~b;
                            sum_b <= 1;
                        end else begin
                            state <= WORK;
                        end
                        s <= sum_out;
                    end

                SUB_B:
                    begin
                        //$display("    SUB, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        state <= INC_Y;
                        sum_a <= x;
                        sum_b <= sum_out;
                    end

                INC_Y:
                    begin
                        //$display("    INC1, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        state <= INC_Y_1;
                        x <= sum_out;
                        sum_a <= y;
                        sum_b <= 1;
                    end

                INC_Y_1:
                    begin
                        //$display("    INC2, s = %d, x = %d, y = %d, b = %d, sum_a = %d, sum_b = %d, sum_out = %d", s, x, y, b, sum_a, sum_b, sum_out);
                        state <= WORK;
                        y <= sum_out;
                    end
            endcase
        end
    end
endmodule