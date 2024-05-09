`timescale 1ns / 1ps
`include "multer.v"

module cqrt(
    input clk,
    input rst,
    input [7:0] a,
    input start,

    output reg [3:0] res,
    output wire busy,

    input wire [7:0] sum_out,
    output reg [7:0] sum_a,
    output reg [7:0] sum_b
    );


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

    assign end_step = (s == -3);
    assign busy = (state != IDLE);

    always@(posedge clk) begin
        if(rst) begin
            state <= IDLE;
        end
        else begin
            case (state)
                IDLE:
                    begin
                        if(start) begin
                            mult_start <= 0;
                            x <= a;
                            s <= 6;
                            y <= 0;
                            state <= WORK;
                        end
                    end

                WORK:
                    begin
                        if(end_step) begin
                            state <= IDLE;
                            res <= y;
                        end else begin
                            y <= y << 1;
                            state <= MULT_PREP_1;
                        end
                    end

                MULT_PREP_1:
                    begin
                        state <= MULT_PREP_2;
                        tmp <= y << 1;
                    end

                MULT_PREP_2:
                    begin
                        state <= MULT_PREP_3;
                        sum_a <= tmp;
                        sum_b <= y;
                    end

                MULT_PREP_3:
                    begin
                        state <= MULT_START_1;
                        tmp <= sum_out;
                        sum_a <= y;
                        sum_b <= 1;
                    end

                MULT_START_1:
                    begin
                        state <= MULT_START_2;
                        mult_a <= tmp;
                        mult_b <= sum_out;
                        mult_start <= 1;
                    end

                MULT_START_2:
                    begin
                        mult_start <= 0;
                        state <= WAIT_MUL;
                    end

                WAIT_MUL:
                    begin
                        if(~mult_busy) begin
                            state <= CALC_B;
                            sum_a <= mult_y;
                            sum_b <= 1;
                        end
                    end

                CALC_B:
                    begin
                        state <= CHECK_X;
                        b <= sum_out << s;
                        sum_a <= s;
                        sum_b <= -3;
                    end

                CHECK_X:
                    begin
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
                        state <= INC_Y;
                        sum_a <= x;
                        sum_b <= sum_out;
                    end

                INC_Y:
                    begin
                        state <= INC_Y_1;
                        x <= sum_out;
                        sum_a <= y;
                        sum_b <= 1;
                    end

                INC_Y_1:
                    begin
                        state <= WORK;
                        y <= sum_out;
                    end
            endcase
        end
    end
endmodule